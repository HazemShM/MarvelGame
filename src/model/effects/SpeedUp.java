package model.effects;

import model.world.Champion;

public class SpeedUp extends Effect{

	public SpeedUp(int duration) {
		super("SpeedUp",duration,EffectType.BUFF);
	}

	@Override
	public void apply(Champion c){
		
		int newSpeed =(int)(c.getSpeed()*(1.15));
		c.setSpeed(newSpeed);
		c.setCurrentActionPoints(c.getCurrentActionPoints()+1);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+1);

		
	
		
	}
	@Override
	public void remove(Champion c) {
		
		int oldSpeed = (int) (c.getSpeed()/1.15 );
		c.setSpeed(oldSpeed);

		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		if(c.getMaxActionPointsPerTurn()!=0)
			c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-1);
		
		
	}

}
