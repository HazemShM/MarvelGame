package model.effects;


import model.abilities.*;
import model.world.Champion;


public class Disarm extends Effect {
	

	public Disarm( int duration) {
		super("Disarm", duration, EffectType.DEBUFF);
		
	}

	
	public void apply(Champion c) {
		
		Ability newAbility = new DamagingAbility("Punch",0,1,1,AreaOfEffect.SINGLETARGET,1,50);
		c.getAbilities().add(newAbility);
		
		
		
		
	}

	
	public void remove(Champion c) {
		
		for (Ability ability : c.getAbilities()) {
			
			if(ability.getName().equals("Punch") ) {
				c.getAbilities().remove(ability);
				break;
			}		
		}
		
		
		
	}
	
}
