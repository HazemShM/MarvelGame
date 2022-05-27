package model.effects;

import model.world.Champion;

public class Shield extends Effect {

	public Shield( int duration) {
		super("Shield", duration, EffectType.BUFF);
		
	}

	@Override
	public void apply(Champion c) {
		
		int newSpeed =(int)(c.getSpeed()*(1.02));
		c.setSpeed(newSpeed);
		
		
	}
	
	@Override
	public void remove(Champion c) {
		
		int oldSpeed = (int) (c.getSpeed()/1.02 );
		c.setSpeed(oldSpeed);
		
	
		
	}

}
