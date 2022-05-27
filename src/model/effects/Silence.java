package model.effects;

import model.world.Champion;

public class Silence extends Effect {

	public Silence( int duration) {
		super("Silence", duration, EffectType.DEBUFF);
		
	}

	@Override
	public void apply(Champion c) {
		
		c.setCurrentActionPoints(c.getCurrentActionPoints()+2);
		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+2);

		
	}

	@Override
	public void remove(Champion c) {
		c.setCurrentActionPoints(c.getCurrentActionPoints()-2);
		if(c.getMaxActionPointsPerTurn()>0) {
			if(c.getMaxActionPointsPerTurn()==1)
				c.setMaxActionPointsPerTurn(0);
			else 
				c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-2);
		}
		


	
		
	}

}
