package model.abilities;

public  class HealingAbility extends Ability {
	private int healAmount;

	public HealingAbility(String name,int cost, int baseCoolDown, int castRadius, AreaOfEffect area,int required, int healingAmount) {
		super(name,cost, baseCoolDown, castRadius, area,required);
		this.healAmount = healingAmount;
	}

	public int getHealAmount() {
		return healAmount;
	}

	public void setHealAmount(int healAmount) {
		this.healAmount = healAmount;
	}

public void execute(ArrayList<Damageable> targets) {
		for(int i=0;i<targets.length;i++) {
		Damageable x=targets[i];
			if (instanceof x==Cover)
			i++;
		else {
		 int NHp= x.getCurrentHp + this.getHealAmount();
		 x.setCurrentHP(NHp);
			
		}
			
			
			
			
		}
		
		
	}

	

}
