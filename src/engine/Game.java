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
			boolean shield = false;
			boolean dodged = false;
			boolean friend = friend(c, t);
			
			if(friend) throw new InvalidTargetException();
			
			for(int i =0 ; i<t.getAppliedEffects().size();i++ ) {
				Effect effect = t.getAppliedEffects().get(i);
				if (effect instanceof Shield ) {
					shield = true;
					effect.remove(t);
					t.getAppliedEffects().remove(i);
					break;
				}
			}
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
				t.setCondition(Condition.KNOCKEDOUT);
				removeFromTurnOrderAndBoard(t);
			}
			
		}
		
			
	}
	public void removeFromTurnOrderAndBoard(Champion c) {
		int size = turnOrder.size();
		PriorityQueue q = new PriorityQueue(size);
		
		for (int i = 0 ; i<size ; i++) {
			Champion inqueChampion  = (Champion) turnOrder.remove();
			if(!inqueChampion.getName().equals(c.getName())) {
				q.insert(inqueChampion);
			}
			
		}
		
		board[c.getLocation().x][c.getLocation().y] = null;
		
	}
	public boolean friend(Champion attacker , Champion target) {
		
		boolean attackerTeam =false;
		boolean targetTeam = false; 
		for(Champion firstTeam : firstPlayer.getTeam()) {
			if(attacker.getName().equals(firstTeam.getName())) attackerTeam = false;
			if(target.getName().equals(firstTeam.getName())) targetTeam = false;
		}
		for(Champion secondTeam : secondPlayer.getTeam()) {
			if(attacker.getName().equals(secondTeam.getName())) attackerTeam = true;
			if(target.getName().equals(secondTeam.getName())) targetTeam = true;
		}
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
					board[target.getLocation().x][target.getLocation().y] = null;
					
				}
			}
		}
		else {
			for(Champion champ : firstPlayer.getTeam()) 
				if(!firstPlayer.getLeader().getName().equals(champ.getName()) && champ.getCondition()!= Condition.KNOCKEDOUT)
					targets.add(champ);
			for(Champion champ : secondPlayer.getTeam()) 
				if(!secondPlayer.getLeader().getName().equals(champ.getName()) && champ.getCondition()!= Condition.KNOCKEDOUT)
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
	public void endTurn() {
		turnOrder.remove();
		if(turnOrder.isEmpty()) {
			prepareChampionTurns();
			
		}
		Champion c = getCurrentChampion();
		boolean stunned =false;
		while( c.getCondition()==Condition.INACTIVE) {
			stunned = false;
			for(Effect effect :  c.getAppliedEffects()) {
				if (effect instanceof Stun) stunned = true;
				if(effect.getDuration()==0) {
					effect.remove( c);
					c.getAppliedEffects().remove(effect);
				}else {
					effect.setDuration(effect.getDuration()-1);
				}
				
			}
			for(Ability ability : c.getAbilities()) {
				if(ability.getCurrentCooldown()==0)
					ability.setCurrentCooldown(ability.getBaseCooldown());
				else ability.setCurrentCooldown(ability.getCurrentCooldown()-1);
			}
			if(c.getCondition() == Condition.INACTIVE)
				turnOrder.remove();
			c = getCurrentChampion();
			
		}
		if (!stunned) {
			for(Effect effect :  c.getAppliedEffects()) {
				if(effect.getDuration()==0) {
					effect.remove( getCurrentChampion());
					c.getAppliedEffects().remove(effect);
				}else {
					effect.setDuration(effect.getDuration()-1);
				}
				
			}
			c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
			for(Ability ability : c.getAbilities()) {
				if(ability.getCurrentCooldown()==0)
					ability.setCurrentCooldown(ability.getBaseCooldown());
				else ability.setCurrentCooldown(ability.getCurrentCooldown()-1);
			}
		}
		
		
		
		
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
	
	public void castAbility(Ability a, int x, int y) throws AbilityUseException, NotEnoughResourcesException, InvalidTargetException, CloneNotSupportedException {
		
		Champion c = getCurrentChampion();
		
		int actionPoints = c.getCurrentActionPoints()-a.getRequiredActionPoints();
		int mana = c.getMana()-a.getManaCost();
		if(actionPoints<0 || mana<0)
			throw new NotEnoughResourcesException();
		
		boolean silence = false;
		
		for (Effect effect : c.getAppliedEffects()) {
			if(effect instanceof Silence)
				silence = true;
		}
		if(silence) throw new AbilityUseException();
		if(a.getCurrentCooldown()!=0) throw new AbilityUseException();
		
		c.setCurrentActionPoints(actionPoints);
		c.setMana(mana);
		a.setCurrentCooldown(a.getBaseCooldown());
		
		Damageable target = (Damageable) board[x][y];
		int distance = Math.abs(x-c.getLocation().x) + Math.abs(y-c.getLocation().y);
		if (distance > a.getCastRange() || target==null) 
			throw new InvalidTargetException();
		ArrayList<Damageable> targets = new ArrayList<>();
		targets.add(target);
		if(target instanceof Cover && a instanceof DamagingAbility) {
			
			a.execute(targets);
			if(target.getCurrentHP()==0)
				board[target.getLocation().x][target.getLocation().y]=null;
			return;
		}
		Champion t = (Champion) target;
		boolean friend = friend(c, t);
		if(a instanceof DamagingAbility) {
			
			
			if(friend) throw new InvalidTargetException();
			
			boolean shield = false;
			for (Effect effect :t.getAppliedEffects()) {
				if(effect instanceof Shield) {
					shield =true;
					effect.remove(t);
					t.getAppliedEffects().remove(effect);
					break;
				}
			}
			if(!shield) {
				a.execute(targets);
				if(target.getCurrentHP()==0)
					removeFromTurnOrderAndBoard((Champion)target);
				
			}
			
		}else if (a instanceof HealingAbility) {
			if(friend) {
				a.execute(targets);
			}else {
				throw new InvalidTargetException();
			}
		}
		else if (a instanceof CrowdControlAbility) {
			if(((CrowdControlAbility) a).getEffect().getType() == EffectType.BUFF && friend)
				a.execute(targets);
			else if (((CrowdControlAbility) a).getEffect().getType() == EffectType.DEBUFF && !friend) 
				a.execute(targets);
			else 
				throw new InvalidTargetException();
			
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
