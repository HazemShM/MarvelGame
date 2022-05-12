package model.abilities;

import model.effects.Effect;
import model.world.Champion;

public class CrowdControlAbility extends Ability {
	private Effect effect;

	public CrowdControlAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area, int required,
			Effect effect) {
		super(name, cost, baseCoolDown, castRadius, area, required);
		this.effect = effect;

	}

	public Effect getEffect() { 
		return effect;
	}
	public void execute(ArrayList<Damageable> targets) {
		for(int i=0;i<targets.length;i++) {
		Champion x=(Champion)targets[i];
		try {
		Effect R=(Effect) effect.clone();
		x.getAppliedEffects().add(R);
			}catch(CloneNotSupportedException e) {}
		
		R.apply(x);
			
		}
			
	}

}
