package engine;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
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
	}
	public Champion getCurrentChampion() {
		Champion c = (Champion) turnOrder.remove();
		turnOrder.insert(c);
		return c;
		
	}
	public Player checkGameOver() {
		int player1 = 0;
		int player2 = 0;
		for (Champion c : firstPlayer.getTeam()) {
			if(c.getCondition()==Condition.KNOCKEDOUT)
				player1++;
		}
		for (Champion c : secondPlayer.getTeam()) {
			if(c.getCondition()==Condition.KNOCKEDOUT)
				player2++;
		}
		if(player1 == 3) return secondPlayer;
		else if (player2 == 3)return firstPlayer;
		else return null;
	}
	public void move(Direction d) throws NotEnoughResourcesException, UnallowedMovementException {
		Champion c = getCurrentChampion();
		if(c.getCurrentActionPoints()==0) throw new NotEnoughResourcesException();
		
		boolean root= false;
		for(Effect effect :c.getAppliedEffects())
			if(effect instanceof Root) root = true;
		if(root==true) throw new UnallowedMovementException();
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
			c.setLocation(newLocation);
		}
	}
	public void attack(Direction d) throws NotEnoughResourcesException, UnallowedMovementException, ChampionDisarmedException, InvalidTargetException {
		Champion c = getCurrentChampion();
		if (c.getCurrentActionPoints()<2) throw new NotEnoughResourcesException();
		
		boolean disarm= false;
		for(Effect effect :c.getAppliedEffects())
			if(effect instanceof Disarm) disarm = true;
		if(disarm==true) throw new ChampionDisarmedException();
		
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
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
				if(target!=null) break;

			}
			
		}else if(d == Direction.DOWN) {
			
			for (int i=c.getLocation().x-1 ; i>= 0 && attackRange>0 && target==null; i-- ) {
				attackRange-- ;
				target =(Damageable) board[i][c.getLocation().y];
				
			}
			
		}
		if(target == null) throw new InvalidTargetException();
		
		if (target instanceof Cover) 
			target.setCurrentHP(target.getCurrentHP()-c.getAttackDamage());
		else if (target instanceof Champion) {
			Champion t = ((Champion)target);
			boolean shield = false;
			boolean dodged = false;
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
					if(chance == 1) {
						dodged = true;
					}
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
