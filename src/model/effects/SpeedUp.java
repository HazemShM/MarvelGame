package model.effects;

import model.world.Champion;

public class SpeedUp extends Effect{

	public SpeedUp(int duration) {
		super("SpeedUp",duration,EffectType.BUFF);
	}


	public void apply(Champion c) {
		
		int newSpeed =(int)(c.getSpeed()*(1.15));
		c.setSpeed(newSpeed);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);
		c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
		
	
		
	}


	public void remove(Champion c) {
		
		int oldSpeed = (int) (c.getSpeed()/1.15 );
		c.setSpeed(oldSpeed);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		
		
		
	}

}
