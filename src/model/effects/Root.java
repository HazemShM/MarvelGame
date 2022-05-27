package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root( int duration) {
		super("Root", duration, EffectType.DEBUFF);
		
	}

	@Override
	public void apply(Champion c) {
		
		if(c.getCondition() != Condition.INACTIVE)
			c.setCondition(Condition.ROOTED);
		

		
		
	}
	@Override
	public void remove(Champion c) {
		
		if (c.getCondition()!= Condition.INACTIVE) {
			
			int root =0 ; 
			for (Effect effect : c.getAppliedEffects() ) {
				if(effect instanceof Root) {
					root ++;
				}

			}
			if (root==0) 
				c.setCondition(Condition.ACTIVE);
			
		}
		
		
	}

}
