package model.effects;

import model.world.Champion;

public class Shock extends Effect {

	public Shock(int duration) {
		super("Shock", duration, EffectType.DEBUFF);
		
	}

	@Override
	public void apply(Champion c) {
		
		int newSpeed =(int)(c.getSpeed()*(0.9));
		c.setSpeed(newSpeed);
		int newAttackDamage = (int)(c.getAttackDamage()*0.9);
		c.setAttackDamage(newAttackDamage);
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		if(c.getMaxActionPointsPerTurn()!=0)
			c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);

		
		
		
	}
	@Override
	public void remove(Champion c) {
		
		int oldSpeed = (int) (c.getSpeed()/0.9 );
		c.setSpeed(oldSpeed);
		int oldAttackDamage = (int)(c.getAttackDamage()/0.9);
		c.setAttackDamage(oldAttackDamage);
		c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);

		

		
	}

}
