package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.BooleanSupplier;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Condition;
import model.world.Cover;
import model.world.Damageable;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;

public class Game {
	private static ArrayList<Champion> availableChampions;
	private static ArrayList<Ability> availableAbilities;
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
		availableChampions = new ArrayList<Champion>();
		availableAbilities = new ArrayList<Ability>();
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		placeChampions();
		placeCovers();
		firstLeaderAbilityUsed = false;
		secondLeaderAbilityUsed = false;
		prepareChampionTurns();
	}
	public Champion getCurrentChampion() {
		
		return (Champion) turnOrder.peekMin();
		
	}
	public Player checkGameOver() {
		boolean player1 = true;
		boolean player2 = true;
		for (Champion c : firstPlayer.getTeam()) {
			if(c.getCondition()!=Condition.KNOCKEDOUT)
				player1 = false;
		}
		for (Champion c : secondPlayer.getTeam()) {
			if(c.getCondition()!=Condition.KNOCKEDOUT)
				player2 = false;
		}
		if(player1) return secondPlayer;
		else if (player2)return firstPlayer;
		else return null;
	}
	
	
	
	

	public void move(Direction d) throws NotEnoughResourcesException, UnallowedMovementException {
		Champion c = getCurrentChampion();
		if(c.getCurrentActionPoints()==0) throw new NotEnoughResourcesException();
		
		if (c.getCondition()==Condition.ROOTED) throw new UnallowedMovementException();
		Point newLocation = null;
				
		if(d == Direction.LEFT && c.getLocation().y-1 >= 0) {
			
			newLocation = new Point(c.getLocation().x , c.getLocation().y-1);
			
		}else if(d == Direction.RIGHT && c.getLocation().y+1 <= 4) {
			
			newLocation = new Point(c.getLocation().x , c.getLocation().y+1);
			
		}else if(d == Direction.UP && c.getLocation().x+1 <= 4) {
			
			newLocation = new Point(c.getLocation().x +1 , c.getLocation().y);
			
		}else if(d == Direction.DOWN && c.getLocation().x-1 >= 0) {
			
			newLocation = new Point(c.getLocation().x-1 , c.getLocation().y);
			
		}
		if (newLocation == null || board[newLocation.x][newLocation.y] != null) 
			throw new UnallowedMovementException();
		else {
			c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
			Point oldLocation = c.getLocation();
			c.setLocation(newLocation);
			board[oldLocation.x][oldLocation.y]=null;
			board[newLocation.x][newLocation.y]=c;
		}
	}
	public void attack(Direction d) throws NotEnoughResourcesException, UnallowedMovementException, ChampionDisarmedException, InvalidTargetException {
		Champion c = getCurrentChampion();
		if (c.getCurrentActionPoints()<2) throw new NotEnoughResourcesException();
		
		boolean disarm= false;
		for(Effect effect :c.getAppliedEffects())
			if(effect instanceof Disarm) disarm = true;
		if(disarm==true) throw new ChampionDisarmedException();
		
		c.setCurrentActionPoints(c.getCurrentActionPoints()-2);
		Damageable target =null;
		int attackRange = c.getAttackRange();
		
		if(d == Direction.LEFT) {
			
			for (int i=c.getLocation().y-1 ; i>= 0 && attackRange>0 && target==null; i-- ) {
				attackRange-- ;
				target =(Damageable) board[c.getLocation().x][i];
				
			}
			
		}else if(d == Direction.RIGHT) {
			
			for (int i=c.getLocation().y+1 ; i<= 4 && attackRange>0 && target==null; i++ ) {
				attackRange-- ;
				target =(Damageable) board[c.getLocation().x][i];

			}
			
		}else if(d == Direction.UP) {

			for (int i=c.getLocation().x+1 ; i<= 4 && attackRange>0 && target==null; i++ ) {
				attackRange-- ;
				target =(Damageable) board[i][c.getLocation().y];

			}
			
		}else if(d == Direction.DOWN) {
			
			for (int i=c.getLocation().x-1 ; i>= 0 && attackRange>0 && target==null; i-- ) {
				attackRange-- ;
				target =(Damageable) board[i][c.getLocation().y];
				
			}
			
		}
		
		
		if (target instanceof Cover) {
			target.setCurrentHP(target.getCurrentHP()-c.getAttackDamage());
			if(target.getCurrentHP()==0) 
				board[target.getLocation().x][target.getLocation().y]=null;
		}
		else if (target instanceof Champion) {
			Champion t = ((Champion)target);
			boolean shield = checkShield(t);
			boolean dodged = false;
			boolean friend = friend(c, t);
			
			if(friend) throw new InvalidTargetException();
			
			
			for(int i =0 ; i<t.getAppliedEffects().size();i++ ) {
				Effect effect = t.getAppliedEffects().get(i);
				if (effect instanceof Dodge ) {
					int chance =(int)( Math.random() * 2 + 1);
					if(chance == 1) 
						dodged = true;
					
					break;
				}
			}
			
			if(!shield && !dodged) {
				int extraDamage =(int)( c.getAttackDamage()*1.5);
				if((c instanceof Hero && t instanceof Villain )||(c instanceof Villain && t instanceof Hero))
					target.setCurrentHP(target.getCurrentHP()-extraDamage);
				else if((c instanceof Hero && t instanceof AntiHero )||(c instanceof AntiHero && t instanceof Hero))
					target.setCurrentHP(target.getCurrentHP()-extraDamage);
				else if ((c instanceof AntiHero && t instanceof Villain )||(c instanceof Villain && t instanceof AntiHero))
					target.setCurrentHP(target.getCurrentHP()-extraDamage);
				else 
					target.setCurrentHP(target.getCurrentHP()-c.getAttackDamage());
			}
			if(target.getCurrentHP()==0) {
				
				removeFromTurnOrderAndBoard(t);
			}
			
		}
		
			
	}
	public void removeFromTurnOrderAndBoard(Champion c) {
		int size = turnOrder.size();
		PriorityQueue q = new PriorityQueue(size);
		
		for (int i = 0 ; i<size ; i++) {
			Champion inqueChampion  = (Champion) turnOrder.remove();
			if(!inqueChampion.equals(c)) {
				q.insert(inqueChampion);
			}
			
		}
		
		board[c.getLocation().x][c.getLocation().y] = null;
		
	}
	public void removeFromTeam (Champion c) {
		if(firstPlayer.getTeam().contains(c))
			firstPlayer.getTeam().remove(c);
		else if(secondPlayer.getTeam().contains(c))
			secondPlayer.getTeam().remove(c);
	}
	
	public boolean friend(Champion attacker , Champion target) {
		
		boolean attackerTeam =getFirstPlayer().getTeam().contains(attacker);
		boolean targetTeam = getFirstPlayer().getTeam().contains(target); 
		
		return attackerTeam==targetTeam;
		
	}
	
	public void useLeaderAbility() throws LeaderNotCurrentException, LeaderAbilityAlreadyUsedException, AbilityUseException {
		Champion c = getCurrentChampion();
		boolean firstPlayerTeam = false;
		boolean secondPlayerTeam = false;
		
		if (c.getName().equals(firstPlayer.getLeader().getName()))
			firstPlayerTeam = true;
		else if (c.getName().equals(secondPlayer.getLeader().getName()))
			secondPlayerTeam = true;
		else throw new LeaderNotCurrentException();
		
		if ((firstPlayerTeam && firstLeaderAbilityUsed )|| (secondPlayerTeam && secondLeaderAbilityUsed))
			throw new LeaderAbilityAlreadyUsedException();
		
		for(Effect effect : c.getAppliedEffects()) {
			if (effect instanceof Silence)
				throw new AbilityUseException();
		}
		ArrayList<Champion> targets = new ArrayList<>();
		
		if(c instanceof Hero) {
			if(firstPlayerTeam) {
				for(Champion champ : firstPlayer.getTeam()) 
					if(champ.getCondition()!= Condition.KNOCKEDOUT)
						targets.add(champ);
			}else {
				for(Champion champ : secondPlayer.getTeam()) 
					if(champ.getCondition()!= Condition.KNOCKEDOUT)
						targets.add(champ);
			}
			c.useLeaderAbility(targets);
			
		}
		else if (c instanceof Villain) {
			if(secondPlayerTeam) {
				for(Champion champ : firstPlayer.getTeam()) 
					if(champ.getCurrentHP() < 0.3 * champ.getMaxHP())
						targets.add(champ);
			}else {
				for(Champion champ : secondPlayer.getTeam()) 
					if(champ.getCurrentHP() < 0.3 * champ.getMaxHP())
						targets.add(champ);
			}
			c.useLeaderAbility(targets);
			for(Champion target : targets) {
				if(target.getCondition() == Condition.KNOCKEDOUT) {
					removeFromTurnOrderAndBoard(target);
					
				}
			}
		}
		else {
			for(Champion champ : firstPlayer.getTeam()) 
				if(!firstPlayer.getLeader().equals(champ) && champ.getCondition()!= Condition.KNOCKEDOUT)
					targets.add(champ);
			for(Champion champ : secondPlayer.getTeam()) 
				if(!secondPlayer.getLeader().equals(champ) && champ.getCondition()!= Condition.KNOCKEDOUT)
					targets.add(champ);
			c.useLeaderAbility(targets);
			for(Champion target : targets) {
				if(target.getCondition() == Condition.KNOCKEDOUT)
					removeFromTurnOrderAndBoard(target);
			}
		}
		if(firstPlayerTeam) firstLeaderAbilityUsed=true;
		else secondLeaderAbilityUsed = true;
		
	}
	
	public void updateEffectsAndAbilities(Champion c) {
		
		for(int i = 0 ; i<c.getAppliedEffects().size();i++) {
			Effect effect =  c.getAppliedEffects().get(i);
			if(effect.getDuration()==1) {
				effect.remove(c);
				c.getAppliedEffects().remove(i);
				i--;
			}else {
				effect.setDuration(effect.getDuration()-1);
			}
			
		}
		for(Ability ability : c.getAbilities()) {
			if(ability.getCurrentCooldown()!=0)
				ability.setCurrentCooldown(ability.getCurrentCooldown()-1);
		}
	}
	
	public void endTurn() {
	
		turnOrder.remove();
		
		if(turnOrder.isEmpty()) {
			prepareChampionTurns();
		}
		
		Champion c = getCurrentChampion();
		
		while(!turnOrder.isEmpty() && c.getCondition()==Condition.INACTIVE) {
			
			
			updateEffectsAndAbilities(c);
			c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
			turnOrder.remove();
			
			c = getCurrentChampion();
		}
		
		updateEffectsAndAbilities(c);
		c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
		
		
		
	}
	private void prepareChampionTurns() {
		
		for(Champion c : firstPlayer.getTeam()) {
			if(c.getCondition() != Condition.KNOCKEDOUT) {
				turnOrder.insert(c);
			}
		}
		for(Champion c : secondPlayer.getTeam()) {
			if(c.getCondition() != Condition.KNOCKEDOUT) {
				turnOrder.insert(c);
			}
		}
		
	}

	public boolean checkShield(Champion t) {
		boolean shield = false;
		for(int i =0 ; i<t.getAppliedEffects().size();i++ ) {
			Effect effect = t.getAppliedEffects().get(i);
			if (effect instanceof Shield ) {
				shield = true;
				effect.remove(t);
				t.getAppliedEffects().remove(i);
				break;
			}
		}
		return shield;
	}

		

	public void castAbility(Ability a, Direction d) throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException, CloneNotSupportedException {
		
		
		Champion c = getCurrentChampion();
		
		int actionPoints = c.getCurrentActionPoints()-a.getRequiredActionPoints();
		int mana = c.getMana()-a.getManaCost();
		
		if(a.getCurrentCooldown()!=0) throw new AbilityUseException();
		
		if(actionPoints<0 || mana<0)
			throw new NotEnoughResourcesException();
		
		
		
		boolean silence = false;
		
		for (Effect effect : c.getAppliedEffects()) {
			if(effect instanceof Silence)
				silence = true;
		}
		
		if(silence) throw new AbilityUseException();		
		
		
		c.setCurrentActionPoints(actionPoints);
		c.setMana(mana);
		a.setCurrentCooldown(a.getBaseCooldown());
		
		int castRange = a.getCastRange();
		ArrayList<Damageable> targets = new ArrayList<>();
		int x;
		int y;
		
		if(d==Direction.LEFT) {
			x=c.getLocation().x;
			y=c.getLocation().y-1;
			
		}else if(d==Direction.RIGHT) {
			x=c.getLocation().x;
			y=c.getLocation().y+1;
			
		}else if(d==Direction.UP) {
			x=c.getLocation().x+1;
			y=c.getLocation().y;
			
		}else {
			x=c.getLocation().x-1;
			y=c.getLocation().y;
			
		}

		if(a instanceof DamagingAbility) {
			while(((d==Direction.LEFT && y>= 0  )||(d==Direction.RIGHT && y<= 4)||(d==Direction.UP && x<= 4  ) || (d==Direction.DOWN && x>= 0 ) ) && castRange >0) {
				
				Damageable target  = (Damageable) board[x][y];
				if (target !=null) {
					if (target instanceof Cover) targets.add(target);
					else {
						boolean friend = friend(c, (Champion) target);
						if(!friend) {
							boolean shield = checkShield((Champion) target);
							if (!shield)
								targets.add(target);
						}
					}
				}
				castRange--;	
				if(d==Direction.LEFT) y--;
				else if(d==Direction.RIGHT) y++;
				else if(d==Direction.UP) x++;
				else x--;
			}
			if(!targets.isEmpty())
				a.execute(targets);
			
			for(Damageable target : targets) {
				if(target.getCurrentHP()==0) {
					if(target instanceof Cover) board[target.getLocation().x][target.getLocation().y]=null;
					else {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}
				}
			}
				
		}else if(a instanceof HealingAbility) {
			while(((d==Direction.LEFT && y>= 0  )||(d==Direction.RIGHT && y<= 4)||(d==Direction.UP && x<= 4  ) || (d==Direction.DOWN && x>= 0 ) ) && castRange >0) {
				Damageable target  = (Damageable) board[x][y];
				if (target !=null && !(target instanceof Cover)) {
					boolean friend = friend(c, (Champion) target);
					if(friend) targets.add(target);
					
				}	
				castRange--;	
				if(d==Direction.LEFT) y--;
				else if(d==Direction.RIGHT) y++;
				else if(d==Direction.UP) x++;
				else x--;
			}
			if(!targets.isEmpty())
				a.execute(targets);
		}else if(a instanceof CrowdControlAbility) {
			while(((d==Direction.LEFT && y>= 0  )||(d==Direction.RIGHT && y<= 4)||(d==Direction.UP && x<= 4  ) || (d==Direction.DOWN && x>= 0 ) ) && castRange >0) {
				Damageable target  = (Damageable) board[x][y];
				if (target !=null && !(target instanceof Cover)) {
					boolean friend = friend(c, (Champion) target);
					if(friend &&((CrowdControlAbility) a).getEffect().getType()==EffectType.BUFF) targets.add(target);
					else if (!friend && ((CrowdControlAbility) a).getEffect().getType()==EffectType.DEBUFF) targets.add(target);
				}
				castRange--;	
				if(d==Direction.LEFT) y--;
				else if(d==Direction.RIGHT) y++;
				else if(d==Direction.UP) x++;
				else x--;
			}
			if(!targets.isEmpty())
				a.execute(targets);
		}
	}
	public void castAbility(Ability a, int x, int y) throws AbilityUseException, NotEnoughResourcesException, InvalidTargetException, CloneNotSupportedException {
		
		Champion c = getCurrentChampion();
		
		int actionPoints = c.getCurrentActionPoints()-a.getRequiredActionPoints();
		int mana = c.getMana()-a.getManaCost();
		if(a.getCurrentCooldown()!=0) throw new AbilityUseException();
		if(actionPoints<0 || mana<0)
			throw new NotEnoughResourcesException();
		
		
		
		boolean silence = false;
		
		for (Effect effect : c.getAppliedEffects()) {
			if(effect instanceof Silence)
				silence = true;
		}
		
		if(silence) throw new AbilityUseException();		
		
		if(c.getLocation().x==x && c.getLocation().y==y && a instanceof DamagingAbility) throw new InvalidTargetException();
		if(c.getLocation().x==x && c.getLocation().y==y && a instanceof CrowdControlAbility && ((CrowdControlAbility)a) .getEffect().getType()==EffectType.DEBUFF) 
			throw new InvalidTargetException();
		c.setCurrentActionPoints(actionPoints);
		c.setMana(mana);
		a.setCurrentCooldown(a.getBaseCooldown());
		
		Damageable target = (Damageable) board[x][y];
		
		int distance = Math.abs(x-c.getLocation().x) + Math.abs(y-c.getLocation().y);
		
		if(target==null) throw new InvalidTargetException();
		
		if (distance > a.getCastRange() ) 
			throw new AbilityUseException();
		
		ArrayList<Damageable> targets = new ArrayList<>();
		targets.add(target);
		
		if(target instanceof Cover && a instanceof DamagingAbility) {
			
			a.execute(targets);
			if(target.getCurrentHP()==0)
				board[target.getLocation().x][target.getLocation().y]=null;
			return;
		}
		
		if(target instanceof Cover && !(a instanceof DamagingAbility)) {
			throw new InvalidTargetException();
		}
		
		Champion t = (Champion) target;
		boolean friend = friend(c, t);
		
		if(a instanceof DamagingAbility) {
			
			if(!friend) {
				boolean shield = checkShield(t);
				
				if(!shield) {
					
					a.execute(targets);
					if(target.getCurrentHP()==0) {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}
				}
			}
		}else if (a instanceof HealingAbility) {
			
			if(friend) {
				a.execute(targets);
			}
		}else if (a instanceof CrowdControlAbility) {
			
			if(((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && friend)
				a.execute(targets);
			else if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && !friend) 
				a.execute(targets);
			else throw new InvalidTargetException();
		}
		
	}
	
	public ArrayList<Damageable> circle(Point c) {

		ArrayList<Damageable> x = new ArrayList<>();
		if(c.x+1>=0 && c.x+1<=4 && c.y<=4 && c.y>=0 ) 
			if(board[c.x+1][c.y]!=null)
			x.add((Damageable)board[c.x+1][c.y]) ;
		
		 if(c.x>=0 && c.x<=4 && c.y+1<=4 && c.y+1>=0 ) 
			if(board[c.x][c.y+1]!=null)
				x.add((Damageable)board[c.x][c.y+1]) ;

		 if(c.x-1>=0 && c.x-1<=4 && c.y<=4 && c.y>=0 ) 
			 if(board[c.x-1][c.y]!=null)
				x.add((Damageable)board[c.x-1][c.y]) ;
			
		 if(c.x>=0 && c.x<=4 && c.y-1<=4 && c.y-1>=0 ) 
			 if(board[c.x][c.y-1]!=null)
				x.add((Damageable)board[c.x][c.y-1]) ;

		if(c.x-1>=0 && c.x-1<=4 && c.y-1<=4 && c.y-1>=0 ) 
			 if(board[c.x-1][c.y-1]!=null)
				x.add((Damageable)board[c.x-1][c.y-1]) ;

		if(c.x+1>=0 && c.x+1<=4 && c.y+1<=4 && c.y+1>=0 ) 
			 if(board[c.x+1][c.y+1]!=null)
				x.add((Damageable)board[c.x+1][c.y+1]) ;			
		if(c.x+1>=0 && c.x+1<=4 && c.y-1<=4 && c.y-1>=0 ) 
			 if(board[c.x+1][c.y-1]!=null)
				x.add((Damageable)board[c.x+1][c.y-1]) ;
			
		if(c.x-1>=0 && c.x-1<=4 && c.y+1<=4 && c.y+1>=0 ) 
			 if(board[c.x-1][c.y+1]!=null)
				x.add((Damageable)board[c.x-1][c.y+1]) ;
		return x;
		
	}

	public void castAbility(Ability a) throws AbilityUseException, NotEnoughResourcesException, InvalidTargetException, CloneNotSupportedException {
	
		Champion c= getCurrentChampion();
		int mana=c.getMana()-a.getManaCost();
		int mypoints=c.getCurrentActionPoints()-a.getRequiredActionPoints();
		if(a.getCurrentCooldown()!=0) throw new AbilityUseException();
		
		if(mana<0 || mypoints<0)
			throw new NotEnoughResourcesException();
		
		boolean silence = false;
		
		for (Effect effect : c.getAppliedEffects()) {
			if(effect instanceof Silence)
				silence = true;
		}
		if(silence) throw new AbilityUseException();
		
		
		c.setCurrentActionPoints(mypoints);
		c.setMana(mana);
		a.setCurrentCooldown(a.getBaseCooldown());
		
		if(a.getCastArea()==AreaOfEffect.SURROUND) {
			
			ArrayList<Damageable> x= this.circle(c.getLocation());
			if (a instanceof DamagingAbility) {
				
				ArrayList<Damageable> targets = new ArrayList<>();
				
				for(int i=0;i<x.size();i++) {
					Damageable target=x.get(i);		
					
					if(target instanceof Cover) {
						targets.add(target);		
					}else if (target instanceof Champion) {
						boolean friend=	friend(c,(Champion)target);
						if(!friend && !checkShield((Champion)target))
								targets.add(target);
					}	
				}
				if(targets.isEmpty()) return;
				a.execute(targets);
				for (Damageable target : targets) {
					if(target instanceof Cover && target.getCurrentHP()==0)
						board[target.getLocation().x][target.getLocation().y]=null;
					else if (target instanceof Champion && target.getCurrentHP()==0) {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}
				}
			}	
			else if (a instanceof HealingAbility)	{
				ArrayList<Damageable> targets = new ArrayList<>();
				
				for(int i=0;i<x.size();i++) {
					Damageable target=x.get(i);	
					if(target instanceof Champion) {
						boolean friend=	friend(c,(Champion)target);
						if(friend) targets.add(target);
					}
				}
				if(targets.isEmpty()) return;
				a.execute(targets);					
			}else if (a instanceof CrowdControlAbility)	{
				ArrayList<Damageable> targets = new ArrayList<>();		
				for(int i=0;i<x.size();i++) {
					Damageable target=x.get(i);		
					if(target instanceof Champion) {
						boolean friend=	friend(c,(Champion)target);
						if(((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && friend && target instanceof Champion) 		
							targets.add(target);
						else if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && !friend  && target instanceof Champion) 
							targets.add(target);	
					}
					
				}
				if(targets.isEmpty()) return;
				a.execute(targets);	
			}
		
		}else if(a.getCastArea()==AreaOfEffect.SELFTARGET) {
			ArrayList<Damageable> targets = new ArrayList<>();
			targets.add(c);
			
			if(a instanceof HealingAbility) 
				a.execute(targets);	

			else if(a instanceof CrowdControlAbility && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF) 
				
				a.execute(targets);
			else throw new InvalidTargetException();	
		
		}else if(a.getCastArea()==AreaOfEffect.TEAMTARGET) {
			
			ArrayList<Champion> team1 = firstPlayer.getTeam();
			ArrayList<Champion> team2 = secondPlayer.getTeam();
			ArrayList<Damageable> targets = new ArrayList<>();
			int distance=0;
			boolean inTeam1 = firstPlayer.getTeam().contains(c);
			if(a instanceof DamagingAbility) {
				if(!inTeam1) {
					for(int i=0;i<team1.size();i++) {
						distance= Math.abs(team1.get(i).getLocation().x-c.getLocation().x) + Math.abs(team1.get(i).getLocation().y-c.getLocation().y);
						if(!checkShield(team1.get(i)) && distance <= a.getCastRange()) 	
							targets.add((Damageable)team1.get(i));
					}
				}else if(inTeam1) {
					for(int i=0;i<team2.size();i++) {
						distance= Math.abs(team2.get(i).getLocation().x-c.getLocation().x) + Math.abs(team2.get(i).getLocation().y-c.getLocation().y);
						if(!checkShield(team2.get(i)) && distance <= a.getCastRange()) 	
							targets.add((Damageable)team2.get(i));
					}
				}	
				if(targets.isEmpty()) return;
				a.execute(targets);	
				
				for (Damageable target : targets) 
					if (target.getCurrentHP()==0) {
						removeFromTurnOrderAndBoard((Champion) target);
						removeFromTeam((Champion) target);
					}	
			}else if(a instanceof HealingAbility) {
				if(inTeam1) {
					for(int i=0;i<team1.size();i++) {
						distance= Math.abs(team1.get(i).getLocation().x-c.getLocation().x) + Math.abs(team1.get(i).getLocation().y-c.getLocation().y);
						if (distance <= a.getCastRange())
							targets.add((Damageable)team1.get(i));
					}
				}else if(!inTeam1) {
					for(int i=0;i<team2.size();i++) {
						distance= Math.abs(team2.get(i).getLocation().x-c.getLocation().x) + Math.abs(team2.get(i).getLocation().y-c.getLocation().y);
						if (distance <= a.getCastRange() ) 
							targets.add((Damageable)team2.get(i));
					}
				}
				if(targets.isEmpty()) return;
				a.execute(targets);	
			}else if(a instanceof CrowdControlAbility) {
				boolean friend1=friend(c,team1.get(0));
				boolean friend2=friend(c,team2.get(0));
				
				ArrayList<Champion> teama;
				if(friend1 && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
					teama=team1;
				else if(friend1 && ((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF)
					teama=team2;
				else if(friend2 && ((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF)
					teama=team2;
				else 
					teama=team1;
				for(int i=0;i<teama.size();i++) {	
					distance= Math.abs(teama.get(i).getLocation().x-c.getLocation().x) + Math.abs(teama.get(i).getLocation().y-c.getLocation().y);
					if (distance <= a.getCastRange() )
						targets.add(teama.get(i));	
				}
				if(targets.isEmpty()) return;
				a.execute(targets);
			}		
				
		}						
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
