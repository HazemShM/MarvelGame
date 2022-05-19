package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
		
	}


	public void apply(Champion c) {
		
		int newSpeed =(int)(c.getSpeed()*(0.9));
		c.setSpeed(newSpeed);
		int newAttackDamage = (int)(c.getAttackDamage()*0.9);
		c.setAttackDamage(newAttackDamage);
		if(c.getMaxActionPointsPerTurn()!=0)
			c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		
		
		
	}
	
	public void remove(Champion c) {
		
		int oldSpeed = (int) (c.getSpeed()/0.9 );
		c.setSpeed(oldSpeed);
		int oldAttackDamage = (int)(c.getAttackDamage()/0.9);
		c.setAttackDamage(oldAttackDamage);
		
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);
		c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
		

		
	}

}
