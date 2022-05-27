package model.effects;

import model.world.Champion;

public class Dodge extends Effect {

	public Dodge(int duration) {
		super("Dodge", duration, EffectType.BUFF);
		
	}

	@Override
	public void apply(Champion c) {
		
		int newSpeed =(int)(c.getSpeed()*(1.05));
		c.setSpeed(newSpeed);
		
		
		
	}

	@Override
	public void remove(Champion c) {
		
		int oldSpeed = (int) (c.getSpeed()/1.05 );
		c.setSpeed(oldSpeed);
		
		
		
	}

}
