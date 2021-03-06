package model.abilities;

import java.util.ArrayList;

import model.world.Champion;
import model.world.Condition;
import model.world.Damageable;

public class DamagingAbility extends Ability {
	
	private int damageAmount;
	public DamagingAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area,int required,int damageAmount) {
		super(name, cost, baseCoolDown, castRadius, area,required);
		this.damageAmount=damageAmount;
	}
	public int getDamageAmount() {
		return damageAmount;
	}
	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}
	
	public void execute(ArrayList<Damageable> targets) {
		
		for(Damageable x : targets) {
			int NHp = x.getCurrentHP() - this.getDamageAmount();
			x.setCurrentHP(NHp);
			if (x instanceof Champion && x.getCurrentHP()==0) 
				((Champion) x).setCondition(Condition.KNOCKEDOUT);
		}
		
	}
	
	

}
