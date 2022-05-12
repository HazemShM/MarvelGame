package model.effects;

import model.abilities.Ability;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;

public class PowerUp extends Effect {
	

	public PowerUp(int duration) {
		super("PowerUp", duration, EffectType.BUFF);
		
	}


	public void apply(Champion c) {
		
		for(Ability ability : c.getAbilities()) {
			if(ability instanceof DamagingAbility) {
				int newDamage = (int)(((DamagingAbility) ability).getDamageAmount() * 1.2);
				((DamagingAbility) ability).setDamageAmount(newDamage);
				
			}
			if(ability instanceof HealingAbility) {
				int newHeal = (int)(((HealingAbility) ability).getHealAmount() * 1.2);
				((HealingAbility) ability).setHealAmount(newHeal);
			}
		}
		
		
		
	}

	
	public void remove(Champion c) {
		
		for(Ability ability : c.getAbilities()) {
			
			if(ability instanceof DamagingAbility) {
				int oldDamage = (int)(((DamagingAbility) ability).getDamageAmount()/1.2);
				((DamagingAbility) ability).setDamageAmount(oldDamage);
				
			}
			if(ability instanceof HealingAbility) {
				int oldHeal = (int)(((HealingAbility) ability).getHealAmount()/1.2);
				((HealingAbility) ability).setHealAmount(oldHeal);
			}
		}
		
		
		
	}
	
}
