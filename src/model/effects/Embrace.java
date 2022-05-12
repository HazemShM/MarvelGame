package model.effects;

import model.world.Champion;

public class Embrace extends Effect {
	

	public Embrace(int duration) {
		super("Embrace", duration, EffectType.BUFF);
	}


	public void apply(Champion c) {
		
		int newHp =(int) (c.getMaxHP() * 0.2 + c.getCurrentHP());
		int newMana = (int)(c.getMana()*0.2 +c.getMana());
		int newSpeed = (int)(c.getSpeed()*0.2 + c.getSpeed());
		int newAttackDamage = (int)(c.getAttackDamage()*0.2 + c.getAttackDamage());
		c.setCurrentHP(newHp);
		c.setMana(newMana);
		c.setSpeed(newSpeed);
		c.setAttackDamage(newAttackDamage);
		
		
		
	}


	public void remove(Champion c) {

		int oldSpeed = (int)(c.getSpeed()/1.2);
		int oldAttackDamage = (int)(c.getAttackDamage()/1.2);
		c.setSpeed(oldSpeed);
		c.setAttackDamage(oldAttackDamage);
		
		
	}

}
