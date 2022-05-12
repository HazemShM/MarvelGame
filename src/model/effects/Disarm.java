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
		
		for (int i = 0; i< c.getAbilities().size() ; i++) {
			
			Ability x = c.getAbilities().get(i);
			if(x.getName().equals("Punch") ) {
				c.getAbilities().remove(i);
				break;
			}		
		}
		
		
		
	}
	
}
