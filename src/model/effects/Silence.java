package model.effects;

import model.world.Champion;

public class Silence extends Effect {

	public Silence( int duration) {
		super("Silence", duration, EffectType.DEBUFF);
		
	}


	public void apply(Champion c) {

		c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()+2);
		c.setCurrentActionPoints(c.getCurrentActionPoints()+2);
		
		
		
	}

	
	public void remove(Champion c) {
		if(c.getMaxActionPointsPerTurn()>0) {
			if(c.getMaxActionPointsPerTurn()==1)
				c.setMaxActionPointsPerTurn(0);
			else 
				c.setMaxActionPointsPerTurn(c.getMaxActionPointsPerTurn()-2);
		}
		
		c.setCurrentActionPoints(c.getCurrentActionPoints()-2);

	
		
	}

}
