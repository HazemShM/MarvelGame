package model.world;

import java.util.ArrayList;
import engine.*;
import model.effects.*;

public class AntiHero extends Champion{

	public AntiHero(String name, int maxHP, int maxMana, int actions, int speed, int attackRange, int attackDamage) {
		super(name, maxHP, maxMana, actions, speed, attackRange, attackDamage);

	}

	public void useLeaderAbility(ArrayList<Champion> targets) {
		
		for(Champion c : targets) {
			Effect stun = new Stun(2);
			stun.apply(c);
			c.getAppliedEffects().add(stun);
		}
		
	}
	
}
