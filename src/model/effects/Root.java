package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root( int duration) {
		super("Root", duration, EffectType.DEBUFF);
		
	}


	public void apply(Champion c) {
		
		if(c.getCondition() != Condition.INACTIVE)
			c.setCondition(Condition.ROOTED);
		

		
		
	}

	public void remove(Champion c) {
		
		if (c.getCondition()!= Condition.INACTIVE) {
			
			int root =0 ; 
			for (Effect effect : c.getAppliedEffects() ) {
				if(effect.getName().compareTo("Root") == 0 && effect.getDuration()!=0) {
					root ++;
				}

			}
			if ((this.getDuration()==0 && root==0 )||(this.getDuration()>0 && root!=1)) 
				c.setCondition(Condition.ACTIVE);
			
		}
		
		
	}

}
