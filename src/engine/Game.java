package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.*;
import model.abilities.*;
import model.effects.*;
import model.world.*;

public class Game {
	private static ArrayList<Champion> availableChampions = new ArrayList<Champion>();
	private static ArrayList<Ability> availableAbilities = new ArrayList<Ability>();;
	private Player firstPlayer;
	private Player secondPlayer;
	private Object[][] board;
	private PriorityQueue turnOrder;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private final static int BOARDWIDTH = 5;
	private final static int BOARDHEIGHT = 5;

	public Game(Player first, Player second) {
		firstPlayer = first;

		secondPlayer = second;

		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		placeChampions();
		placeCovers();
		firstLeaderAbilityUsed = false;
		secondLeaderAbilityUsed = false;
		prepareChampionTurns();
	}

	public Champion getCurrentChampion() {

		if (turnOrder.isEmpty())
			return null;
		return (Champion) turnOrder.peekMin();

	}

	public Player checkGameOver() {
		boolean player1 = true;
		boolean player2 = true;
		for (Champion c : getFirstPlayer().getTeam()) {
			if (c.getCondition() != Condition.KNOCKEDOUT)
				player1 = false;
		}
		for (Champion c : getSecondPlayer().getTeam()) {
			if (c.getCondition() != Condition.KNOCKEDOUT)
				player2 = false;
		}
		if (player1)
			return getSecondPlayer();
		else if (player2)
			return getFirstPlayer();
		else
			return null;
	}

	public void move(Direction d) throws NotEnoughResourcesException, UnallowedMovementException {
		Champion c = getCurrentChampion();
		if (c==null) return;
		
		if (c.getCurrentActionPoints() == 0)
			throw new NotEnoughResourcesException("You need at least one action point to move");

		if (c.getCondition() == Condition.ROOTED)
			throw new UnallowedMovementException("You can not move while being rooted");
		Point newLocation = null;

		if (d == Direction.LEFT && c.getLocation().y - 1 >= 0) {

			newLocation = new Point(c.getLocation().x, c.getLocation().y - 1);

		} else if (d == Direction.RIGHT && c.getLocation().y + 1 <= 4) {

			newLocation = new Point(c.getLocation().x, c.getLocation().y + 1);

		} else if (d == Direction.UP && c.getLocation().x + 1 <= 4) {

			newLocation = new Point(c.getLocation().x + 1, c.getLocation().y);

		} else if (d == Direction.DOWN && c.getLocation().x - 1 >= 0) {

			newLocation = new Point(c.getLocation().x - 1, c.getLocation().y);

		}
		if (newLocation == null )
			throw new UnallowedMovementException("Can not move out of the board");
		else if (getBoard()[newLocation.x][newLocation.y] != null)
			throw new UnallowedMovementException("target cell is not empty");
		else {
			c.setCurrentActionPoints(c.getCurrentActionPoints() - 1);
			Point oldLocation = c.getLocation();
			c.setLocation(newLocation);
			getBoard()[oldLocation.x][oldLocation.y] = null;
			getBoard()[newLocation.x][newLocation.y] = c;
		}
	}

	public void attack(Direction d) throws NotEnoughResourcesException, UnallowedMovementException,
			ChampionDisarmedException, InvalidTargetException {
		Champion c = getCurrentChampion();
		if (c==null) return;
		if (c.getCurrentActionPoints() < 2)
			throw new NotEnoughResourcesException("You need at least two action point to perform a normal attack");

		for (Effect effect : c.getAppliedEffects())
			if (effect instanceof Disarm)
				throw new ChampionDisarmedException("Can not attack while being disarmed");
		

		c.setCurrentActionPoints(c.getCurrentActionPoints() - 2);
		Damageable target = null;
		int attackRange = c.getAttackRange();

		if (d == Direction.LEFT) {

			for (int i = c.getLocation().y - 1; i >= 0 && attackRange > 0 && target == null; i--) {
				attackRange--;
				target = (Damageable) getBoard()[c.getLocation().x][i];

			}

		}else if (d == Direction.RIGHT) {

			for (int i = c.getLocation().y + 1; i <= 4 && attackRange > 0 && target == null; i++) {
				attackRange--;
				target = (Damageable) getBoard()[c.getLocation().x][i];

			}

		} else if (d == Direction.UP) {

			for (int i = c.getLocation().x + 1; i <= 4 && attackRange > 0 && target == null; i++) {
				attackRange--;
				target = (Damageable) getBoard()[i][c.getLocation().y];

			}

		} else if (d == Direction.DOWN) {

			for (int i = c.getLocation().x - 1; i >= 0 && attackRange > 0 && target == null; i--) {
				attackRange--;
				target = (Damageable) getBoard()[i][c.getLocation().y];

			}

		}

		if (target instanceof Cover) {
			target.setCurrentHP(target.getCurrentHP() - c.getAttackDamage());
			if (target.getCurrentHP() == 0)
				getBoard()[target.getLocation().x][target.getLocation().y] = null;
		} else if (target instanceof Champion) {

			boolean friend = friend(c, ((Champion) target));
			if (friend)
				return;

			boolean shield = checkShield(((Champion) target));
			boolean dodged = false;

			for (int i = 0; i < ((Champion) target).getAppliedEffects().size(); i++) {
				Effect effect = ((Champion) target).getAppliedEffects().get(i);
				if (effect instanceof Dodge) {
					int chance = (int) (Math.random() * 2 + 1);
					if (chance == 1)
						dodged = true;

					break;
				}
			}

			if (!shield && !dodged) {
				int extraDamage = (int) (c.getAttackDamage() * 1.5);

				if (c.getClass() != target.getClass())
					target.setCurrentHP(target.getCurrentHP() - extraDamage);
				else
					target.setCurrentHP(target.getCurrentHP() - c.getAttackDamage());
			}
			if (target.getCurrentHP() == 0) {
				((Champion) target).setCondition(Condition.KNOCKEDOUT);
				removeFromTurnOrderAndBoard(((Champion) target));
				removeFromTeam(((Champion) target));
			}

		}
	}

	public boolean checkShield(Champion t) {
		int min = Integer.MAX_VALUE;
		boolean shield = false;
		
		for(Effect effect: t.getAppliedEffects()) {
			if(effect instanceof Shield) {
				min = Math.min(min, effect.getDuration());
				shield = true;
			}
		}
		if(shield) {
			for (Effect effect : t.getAppliedEffects()) {
				if (effect instanceof Shield && effect.getDuration()==min) {
					t.getAppliedEffects().remove(effect);
					effect.remove(t);
					break;
				}
			}
		}
		return shield;
	}

	public void removeFromTurnOrderAndBoard(Champion c) {
		PriorityQueue q = new PriorityQueue(getTurnOrder().size());

		while (!getTurnOrder().isEmpty()) {
			Champion inqueChampion = (Champion) getTurnOrder().remove();
			if (!inqueChampion.equals(c)) {
				q.insert(inqueChampion);
			}

		}
		while (!q.isEmpty())
			getTurnOrder().insert(q.remove());

		getBoard()[c.getLocation().x][c.getLocation().y] = null;
		c.setLocation(null);

	}

	public void removeFromTeam(Champion c) {
		if (getFirstPlayer().getTeam().contains(c))
			getFirstPlayer().getTeam().remove(c);
		else if (getSecondPlayer().getTeam().contains(c))
			getSecondPlayer().getTeam().remove(c);
	}

	public boolean friend(Champion attacker, Champion target) {

		boolean attackerTeam = getFirstPlayer().getTeam().contains(attacker);
		boolean targetTeam = getFirstPlayer().getTeam().contains(target);

		return attackerTeam == targetTeam;

	}

	public void useLeaderAbility() throws LeaderNotCurrentException, LeaderAbilityAlreadyUsedException,
			AbilityUseException, InvalidTargetException {
		Champion c = getCurrentChampion();
		if (c==null) return;
		boolean firstPlayerTeam = false;
		boolean secondPlayerTeam = false;

		if (getFirstPlayer().getLeader().equals(c))
			firstPlayerTeam = true;
		else if (getSecondPlayer().getLeader().equals(c))
			secondPlayerTeam = true;
		else
			throw new LeaderNotCurrentException("The current champion is not a leader");

		if ((firstPlayerTeam && isFirstLeaderAbilityUsed()) || (secondPlayerTeam && isSecondLeaderAbilityUsed()))
			throw new LeaderAbilityAlreadyUsedException("This leader already used his ability");

		for (Effect effect : c.getAppliedEffects()) {
			if (effect instanceof Silence)
				throw new AbilityUseException();
		}
		if (firstPlayerTeam)
			firstLeaderAbilityUsed = true;
		else
			secondLeaderAbilityUsed = true;
		ArrayList<Champion> targets = new ArrayList<>();

		if (c instanceof Hero) {
			if (firstPlayerTeam) {
				for (Champion champ : getFirstPlayer().getTeam())
					if (champ.getCondition() != Condition.KNOCKEDOUT)
						targets.add(champ);
			} else {
				for (Champion champ : getSecondPlayer().getTeam())
					if (champ.getCondition() != Condition.KNOCKEDOUT)
						targets.add(champ);
			}
			c.useLeaderAbility(targets);

		} else if (c instanceof Villain) {
			if (secondPlayerTeam) {
				for (Champion champ : getFirstPlayer().getTeam())
					if (champ.getCurrentHP() < 0.3 * champ.getMaxHP())
						targets.add(champ);
			} else {
				for (Champion champ : getSecondPlayer().getTeam())
					if (champ.getCurrentHP() < 0.3 * champ.getMaxHP())
						targets.add(champ);
			}
			c.useLeaderAbility(targets);
			for (Champion target : targets) {

				removeFromTurnOrderAndBoard(target);
				removeFromTeam(target);

			}
		} else {
			for (Champion champ : getFirstPlayer().getTeam())
				if (!getFirstPlayer().getLeader().equals(champ))
					targets.add(champ);
			for (Champion champ : getSecondPlayer().getTeam())
				if (!getSecondPlayer().getLeader().equals(champ))
					targets.add(champ);
			c.useLeaderAbility(targets);

		}

	}

	public void updateEffectsAndAbilities(Champion c) {
		if (c==null) return;
		
		for (int i = 0; i < c.getAppliedEffects().size(); i++) {
			Effect effect = c.getAppliedEffects().get(i);
			if (effect.getDuration() == 1) {
				c.getAppliedEffects().remove(i);
				effect.remove(c);
				i--;
			} else {
				effect.setDuration(effect.getDuration() - 1);
			}

		}
		for (Ability ability : c.getAbilities()) {
			if (ability.getCurrentCooldown() != 0)
				ability.setCurrentCooldown(ability.getCurrentCooldown() - 1);
		}
		c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
	}

	public void endTurn() {

		turnOrder.remove();

		if (turnOrder.isEmpty()) {
			prepareChampionTurns();
		}

		Champion c = getCurrentChampion();

		while (!turnOrder.isEmpty() && c.getCondition() == Condition.INACTIVE) {

			updateEffectsAndAbilities(c);
			turnOrder.remove();

			if (turnOrder.isEmpty()) {
				prepareChampionTurns();
			}

			c = getCurrentChampion();
		}

		updateEffectsAndAbilities(c);

	}

	private void prepareChampionTurns() {

		for (Champion c : firstPlayer.getTeam()) {
			if (c.getCondition() != Condition.KNOCKEDOUT) {
				turnOrder.insert(c);
			}
		}
		for (Champion c : secondPlayer.getTeam()) {
			if (c.getCondition() != Condition.KNOCKEDOUT) {
				turnOrder.insert(c);
			}
		}

	}

	public void castAbility(Ability a, Direction d) throws NotEnoughResourcesException, AbilityUseException,
			InvalidTargetException, CloneNotSupportedException {

		Champion c = getCurrentChampion();
		if (c==null) return;
		int actionPoints = c.getCurrentActionPoints() - a.getRequiredActionPoints();
		int mana = c.getMana() - a.getManaCost();

		if (a.getCurrentCooldown() != 0)
			throw new AbilityUseException("You can not use an ability while it is in cooldown");

		if (mana < 0 )
			throw new NotEnoughResourcesException("you need at least " + a.getManaCost() + " mana to cast this ability");
		else if ( actionPoints < 0)
			throw new NotEnoughResourcesException("you need at least " + a.getRequiredActionPoints() + " action points to cast this ability");
		
		for (Effect effect : c.getAppliedEffects()) {
			if (effect instanceof Silence)
				throw new AbilityUseException("You can not cast an ability while being silenced");
		}


		c.setCurrentActionPoints(actionPoints);
		c.setMana(mana);
		a.setCurrentCooldown(a.getBaseCooldown());

		int castRange = a.getCastRange();
		ArrayList<Damageable> targets = new ArrayList<>();
		int x;
		int y;

		if (d == Direction.LEFT) {
			x = c.getLocation().x;
			y = c.getLocation().y - 1;

		} else if (d == Direction.RIGHT) {
			x = c.getLocation().x;
			y = c.getLocation().y + 1;

		} else if (d == Direction.UP) {
			x = c.getLocation().x + 1;
			y = c.getLocation().y;

		} else {
			x = c.getLocation().x - 1;
			y = c.getLocation().y;

		}
		if (a instanceof DamagingAbility) {
			while (((d == Direction.LEFT && y >= 0) || (d == Direction.RIGHT && y <= 4) || (d == Direction.UP && x <= 4)
					|| (d == Direction.DOWN && x >= 0)) && castRange > 0) {

				Damageable target = (Damageable) board[x][y];
				if (target != null) {
					if (target instanceof Cover)
						targets.add(target);
					else {
						boolean friend = friend(c, (Champion) target);
						if (!friend) {
							boolean shield = checkShield((Champion) target);
							if (!shield)
								targets.add(target);
						}
					}
				}
				castRange--;
				if (d == Direction.LEFT)
					y--;
				else if (d == Direction.RIGHT)
					y++;
				else if (d == Direction.UP)
					x++;
				else
					x--;
			}
			a.execute(targets);

			for (Damageable target : targets) {
				if (target.getCurrentHP() == 0) {
					if (target instanceof Cover)
						board[target.getLocation().x][target.getLocation().y] = null;
					else {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}
				}
			}

		} else if (a instanceof HealingAbility) {
			while (((d == Direction.LEFT && y >= 0) || (d == Direction.RIGHT && y <= 4) || (d == Direction.UP && x <= 4)
					|| (d == Direction.DOWN && x >= 0)) && castRange > 0) {
				Damageable target = (Damageable) board[x][y];
				if (target != null && target instanceof Champion) {
					boolean friend = friend(c, (Champion) target);
					if (friend)
						targets.add(target);

				}
				castRange--;
				if (d == Direction.LEFT)
					y--;
				else if (d == Direction.RIGHT)
					y++;
				else if (d == Direction.UP)
					x++;
				else
					x--;
			}
			a.execute(targets);

		} else if (a instanceof CrowdControlAbility) {
			while (((d == Direction.LEFT && y >= 0) || (d == Direction.RIGHT && y <= 4) || (d == Direction.UP && x <= 4)
					|| (d == Direction.DOWN && x >= 0)) && castRange > 0) {
				Damageable target = (Damageable) board[x][y];
				if (target != null && target instanceof Champion) {
					boolean friend = friend(c, (Champion) target);
					if (friend && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
						targets.add(target);
					else if (!friend && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)
						targets.add(target);
				}
				castRange--;
				if (d == Direction.LEFT)
					y--;
				else if (d == Direction.RIGHT)
					y++;
				else if (d == Direction.UP)
					x++;
				else
					x--;
			}
			a.execute(targets);
		}
	}

	public void castAbility(Ability a, int x, int y) throws AbilityUseException, NotEnoughResourcesException,
			InvalidTargetException, CloneNotSupportedException {

		Champion c = getCurrentChampion();
		if (c==null) return;
		int actionPoints = c.getCurrentActionPoints() - a.getRequiredActionPoints();
		int mana = c.getMana() - a.getManaCost();

		if (a.getCurrentCooldown() != 0)
			throw new AbilityUseException("You can not use an ability while it is in cooldown");

		if (mana < 0 )
			throw new NotEnoughResourcesException("you need at least " + a.getManaCost() + " mana to cast this ability");
		else if ( actionPoints < 0)
			throw new NotEnoughResourcesException("you need at least " + a.getRequiredActionPoints() + " action points to cast this ability");
		
		for (Effect effect : c.getAppliedEffects()) {
			if (effect instanceof Silence)
				throw new AbilityUseException("You can not cast an ability while being silenced");
		}

		Damageable target = (Damageable) board[x][y];

		int distance = Math.abs(x - c.getLocation().x) + Math.abs(y - c.getLocation().y);

		if (target == null)
			throw new InvalidTargetException("You can not cast an ability on an empty cell");

		if (distance > a.getCastRange())
			throw new AbilityUseException("Target out of the ability's cast range");

		ArrayList<Damageable> targets = new ArrayList<>();
		targets.add(target);

		if (target instanceof Cover && a instanceof DamagingAbility) {

			a.execute(targets);
			if (target.getCurrentHP() == 0)
				board[target.getLocation().x][target.getLocation().y] = null;
			
		}
		if (target instanceof Cover && !(a instanceof DamagingAbility)) {
			throw new InvalidTargetException("Covers can only be damaged");
		}
		if(target instanceof Champion) {
			Champion t = (Champion) target;
			boolean friend = friend(c, t);

			if (a instanceof DamagingAbility) {

				if (!friend) {
					boolean shield = checkShield(t);

					if (!shield) {

						a.execute(targets);
						if (target.getCurrentHP() == 0) {
							removeFromTurnOrderAndBoard((Champion) target);
							removeFromTeam((Champion) target);
						}
					}
				} else {
					throw new InvalidTargetException("Can not cast damaging ability on friendly targets");
				}
			} else if (a instanceof HealingAbility) {

				if (friend) {
					a.execute(targets);
				} else {
					throw new InvalidTargetException("Can not cast healing ability on enemy targets");
				}
			} else if (a instanceof CrowdControlAbility) {

				if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && friend)
					a.execute(targets);
				else if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && !friend)
					a.execute(targets);
				else if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && friend)
					throw new InvalidTargetException("Can not debuff friendly targets");
				else  if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && !friend)
					throw new InvalidTargetException("Can not buff enemy targets");
			}
		}
		c.setCurrentActionPoints(c.getCurrentActionPoints() - a.getRequiredActionPoints());
		a.setCurrentCooldown(a.getBaseCooldown());
		c.setMana(c.getMana() - a.getManaCost());

	}

	public ArrayList<Damageable> circle(Point c) {

		ArrayList<Damageable> x = new ArrayList<>();
		if (c.x + 1 >= 0 && c.x + 1 <= 4 && c.y <= 4 && c.y >= 0)
			if (board[c.x + 1][c.y] != null)
				x.add((Damageable) board[c.x + 1][c.y]);

		if (c.x >= 0 && c.x <= 4 && c.y + 1 <= 4 && c.y + 1 >= 0)
			if (board[c.x][c.y + 1] != null)
				x.add((Damageable) board[c.x][c.y + 1]);

		if (c.x - 1 >= 0 && c.x - 1 <= 4 && c.y <= 4 && c.y >= 0)
			if (board[c.x - 1][c.y] != null)
				x.add((Damageable) board[c.x - 1][c.y]);

		if (c.x >= 0 && c.x <= 4 && c.y - 1 <= 4 && c.y - 1 >= 0)
			if (board[c.x][c.y - 1] != null)
				x.add((Damageable) board[c.x][c.y - 1]);

		if (c.x - 1 >= 0 && c.x - 1 <= 4 && c.y - 1 <= 4 && c.y - 1 >= 0)
			if (board[c.x - 1][c.y - 1] != null)
				x.add((Damageable) board[c.x - 1][c.y - 1]);

		if (c.x + 1 >= 0 && c.x + 1 <= 4 && c.y + 1 <= 4 && c.y + 1 >= 0)
			if (board[c.x + 1][c.y + 1] != null)
				x.add((Damageable) board[c.x + 1][c.y + 1]);
		if (c.x + 1 >= 0 && c.x + 1 <= 4 && c.y - 1 <= 4 && c.y - 1 >= 0)
			if (board[c.x + 1][c.y - 1] != null)
				x.add((Damageable) board[c.x + 1][c.y - 1]);

		if (c.x - 1 >= 0 && c.x - 1 <= 4 && c.y + 1 <= 4 && c.y + 1 >= 0)
			if (board[c.x - 1][c.y + 1] != null)
				x.add((Damageable) board[c.x - 1][c.y + 1]);
		return x;

	}

	public void castAbility(Ability a) throws AbilityUseException, NotEnoughResourcesException, InvalidTargetException,CloneNotSupportedException {

		Champion c = getCurrentChampion();
		if (c==null) return;
		
		int mana = c.getMana() - a.getManaCost();
		int mypoints = c.getCurrentActionPoints() - a.getRequiredActionPoints();

		if (a.getCurrentCooldown() != 0)
			throw new AbilityUseException("You can not use an ability while it is in cooldown");

		if (mana < 0 )
			throw new NotEnoughResourcesException("you need at least " + a.getManaCost() + " mana to cast this ability");
		else if ( mypoints < 0)
			throw new NotEnoughResourcesException("you need at least " + a.getRequiredActionPoints() + " action points to cast this ability");
		
		for (Effect effect : c.getAppliedEffects()) {
			if (effect instanceof Silence)
				throw new AbilityUseException("You can not cast an ability while being silenced");
		}
		a.setCurrentCooldown(a.getBaseCooldown());
		c.setCurrentActionPoints( c.getCurrentActionPoints() - a.getRequiredActionPoints());
		
		ArrayList<Damageable> targets = new ArrayList<>();

		if (a.getCastArea() == AreaOfEffect.SURROUND) {

			ArrayList<Damageable> x = this.circle(c.getLocation());
			if (a instanceof DamagingAbility) {

				for (Damageable target : x) {

					if (target instanceof Cover) {
						targets.add(target);
					} else if (target instanceof Champion) {
						boolean friend = friend(c, (Champion) target);
						if (!friend && !checkShield((Champion) target))
							targets.add(target);
					}
				}

				a.execute(targets);

				for (Damageable target : targets) {
					
					if (target instanceof Cover && target.getCurrentHP() == 0)
						board[target.getLocation().x][target.getLocation().y] = null;
					else if (target instanceof Champion && target.getCurrentHP() == 0) {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}
				}
			} else if (a instanceof HealingAbility) {
				for (Damageable target : x) {
					if (target instanceof Champion) {
						boolean friend = friend(c, (Champion) target);
						if (friend)
							targets.add(target);
					}
				}

				a.execute(targets);
			} else if (a instanceof CrowdControlAbility) {

				for (Damageable target : x) {
					if (target instanceof Champion) {
						boolean friend = friend(c, (Champion) target);
						if (((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && friend)
							targets.add(target);
						else if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && !friend)
							targets.add(target);
					}

				}
				a.execute(targets);
			}

		} else if (a.getCastArea() == AreaOfEffect.SELFTARGET) {

			targets.add(c);

			if (a instanceof HealingAbility)
				a.execute(targets);
			else if (a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
				a.execute(targets);

		}else if (a.getCastArea() == AreaOfEffect.TEAMTARGET) {

			int distance = 0;
			boolean inTeam1 = firstPlayer.getTeam().contains(c);
			
			if (a instanceof DamagingAbility) {
				if (!inTeam1) {
					for (Champion target : firstPlayer.getTeam()) {
						distance = Math.abs(target.getLocation().x - c.getLocation().x)
								+ Math.abs(target.getLocation().y - c.getLocation().y);
						if (distance <= a.getCastRange() && !checkShield(target))
							targets.add(target);
					}
				} else if (inTeam1) {
					for (Champion target : secondPlayer.getTeam()) {
						distance = Math.abs(target.getLocation().x - c.getLocation().x)
								+ Math.abs(target.getLocation().y - c.getLocation().y);
						if (distance <= a.getCastRange() && !checkShield(target))
							targets.add(target);
					}
				}

				a.execute(targets);

				for (Damageable target : targets)
					if (target.getCurrentHP() == 0) {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}
			} else if (a instanceof HealingAbility) {
				if (inTeam1) {
					for (Champion target : firstPlayer.getTeam()) {
						distance = Math.abs(target.getLocation().x - c.getLocation().x)
								+ Math.abs(target.getLocation().y - c.getLocation().y);
						if (distance <= a.getCastRange())
							targets.add(target);
					}
				} else if (!inTeam1) {
					for (Champion target : secondPlayer.getTeam()) {
						distance = Math.abs(target.getLocation().x - c.getLocation().x)
								+ Math.abs(target.getLocation().y - c.getLocation().y);
						if (distance <= a.getCastRange())
							targets.add(target);
					}
				}

				a.execute(targets);
			} else if (a instanceof CrowdControlAbility) {
				ArrayList<Champion> teama;
				
				if (inTeam1 && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
					teama = firstPlayer.getTeam();
				else if (inTeam1 && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)
					teama = secondPlayer.getTeam();
				else if (!inTeam1 && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
					teama = secondPlayer.getTeam();
				else
					teama = firstPlayer.getTeam();
				
				for (Champion target : teama) {
					distance = Math.abs(target.getLocation().x - c.getLocation().x)
							+ Math.abs(target.getLocation().y - c.getLocation().y);
					if (distance <= a.getCastRange())
						targets.add(target);
				}

				a.execute(targets);
			}

		}
		
		c.setMana(c.getMana() - a.getManaCost());
	}

	public static void loadAbilities(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Ability a = null;
			AreaOfEffect ar = null;
			switch (content[5]) {
			case "SINGLETARGET":
				ar = AreaOfEffect.SINGLETARGET;
				break;
			case "TEAMTARGET":
				ar = AreaOfEffect.TEAMTARGET;
				break;
			case "SURROUND":
				ar = AreaOfEffect.SURROUND;
				break;
			case "DIRECTIONAL":
				ar = AreaOfEffect.DIRECTIONAL;
				break;
			case "SELFTARGET":
				ar = AreaOfEffect.SELFTARGET;
				break;

			}
			Effect e = null;
			if (content[0].equals("CC")) {
				switch (content[7]) {
				case "Disarm":
					e = new Disarm(Integer.parseInt(content[8]));
					break;
				case "Dodge":
					e = new Dodge(Integer.parseInt(content[8]));
					break;
				case "Embrace":
					e = new Embrace(Integer.parseInt(content[8]));
					break;
				case "PowerUp":
					e = new PowerUp(Integer.parseInt(content[8]));
					break;
				case "Root":
					e = new Root(Integer.parseInt(content[8]));
					break;
				case "Shield":
					e = new Shield(Integer.parseInt(content[8]));
					break;
				case "Shock":
					e = new Shock(Integer.parseInt(content[8]));
					break;
				case "Silence":
					e = new Silence(Integer.parseInt(content[8]));
					break;
				case "SpeedUp":
					e = new SpeedUp(Integer.parseInt(content[8]));
					break;
				case "Stun":
					e = new Stun(Integer.parseInt(content[8]));
					break;
				}
			}
			switch (content[0]) {
			case "CC":
				a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
				break;
			case "DMG":
				a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			case "HEL":
				a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			}
			availableAbilities.add(a);
			line = br.readLine();
		}
		br.close();
	}

	public static void loadChampions(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Champion c = null;
			switch (content[0]) {
			case "A":
				c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;

			case "H":
				c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			case "V":
				c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			}

			c.getAbilities().add(findAbilityByName(content[8]));
			c.getAbilities().add(findAbilityByName(content[9]));
			c.getAbilities().add(findAbilityByName(content[10]));
			availableChampions.add(c);
			line = br.readLine();
		}
		br.close();
	}

	private static Ability findAbilityByName(String name) {
		for (Ability a : availableAbilities) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	public void placeCovers() {
		int i = 0;
		while (i < 5) {
			int x = ((int) (Math.random() * (BOARDWIDTH - 2))) + 1;
			int y = (int) (Math.random() * BOARDHEIGHT);

			if (board[x][y] == null) {
				board[x][y] = new Cover(x, y);
				i++;
			}
		}

	}

	public void placeChampions() {
		int i = 1;
		for (Champion c : firstPlayer.getTeam()) {
			board[0][i] = c;
			c.setLocation(new Point(0, i));
			i++;
		}
		i = 1;
		for (Champion c : secondPlayer.getTeam()) {
			board[BOARDHEIGHT - 1][i] = c;
			c.setLocation(new Point(BOARDHEIGHT - 1, i));
			i++;
		}

	}

	public static ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public static ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public Object[][] getBoard() {
		return board;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public static int getBoardwidth() {
		return BOARDWIDTH;
	}

	public static int getBoardheight() {
		return BOARDHEIGHT;
	}
}
