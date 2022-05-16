package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Stun extends Effect {

	public Stun(int duration) {
		super("Stun", duration, EffectType.DEBUFF);
	}


	public void apply(Champion c) {
		
		c.setCondition(Condition.INACTIVE);
		
		
	}

	
	public void remove(Champion c) {
		int countStun = 0;
		int countRoot = 0;
		
		for(Effect effect : c.getAppliedEffects())
			if(effect instanceof Stun && !this.equals(effect)) 
				countStun++;
			else if(effect instanceof Root) 
				countRoot++;
		
		if(countStun==0) {
			if(countRoot==0) c.setCondition(Condition.ACTIVE);
			else c.setCondition(Condition.ROOTED);
		}
		
	}


}
