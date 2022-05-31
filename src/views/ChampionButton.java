package views;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.abilities.Ability;
import model.abilities.*;
import model.world.Champion;

public class ChampionButton {
	Button championButton;
	Champion champion;
	Boolean pressed; 
	Image img;
	public ChampionButton(Champion c){
		champion =c;
		championButton = new Button();
		championButton.setMaxSize(150, 150);
		
		String name = "/resources/"+c.getName()+".png";
		img = new Image(name);
		ImageView view = new ImageView(img);
		championButton.setGraphic(view);

		pressed = false;
		championButton.setOnAction(e ->{
			choosechamps.Choosen.getChildren().remove(choosechamps.label);
			choosechamps.label = new Label();
			choosechamps.label.setText(getInfo());
			choosechamps.Choosen.getChildren().addAll( choosechamps.label);
			
		});

	}
	
	public void place(int x , int y) {
		GridPane.setConstraints(championButton,x,y);
	}
	public String getInfo() {
		String info = "";
		info += "Name: "+ champion.getName() + "\n";
		info += "Max HP: " + champion.getMaxHP() + "\n";
		info += "Mana: " + champion.getMana()  + "\n";
		info += "Max ActionPoints: " + champion.getMaxActionPointsPerTurn() + "\n";
		info += "Speed: " + champion.getSpeed()  + "\n";
		info += "Attack Range: " +champion.getAttackRange()  + "\n";
		info += "Attack Damage: " +champion.getAttackDamage() + "\n";
		info +=   "\n";
		info += "Champion Abilities: "  + "\n";
		int c=1;
		for (Ability a : champion.getAbilities()) {
			info+= "	Ability " + c +": "  + "\n";
			info+= "		-Name: "+ a.getName() +  "\n";
			info+= "		-Mana Cost: " + a.getManaCost()  + "\n";
			info+= "		-BaseCoolDown: " +a.getBaseCooldown() + "\n";
			info+= "		-Range: " +a.getCastRange() + "\n";
			info+= "		-Area Of Effect: "+a.getCastArea() + "\n";
			info+= "		-Required AtionPoints: "+a.getRequiredActionPoints() + "\n";
		
			if(a instanceof CrowdControlAbility) {
				info+= "		-Type: CrowdControlAbility"  + "\n";
				info+= "		-Effect:- Name: "+ ((CrowdControlAbility)a).getEffect().getName()+
						", Duration: " +((CrowdControlAbility)a).getEffect().getDuration() + 
						" EffectType: "+((CrowdControlAbility)a).getEffect().getType()  + "\n";
				
			}else if(a instanceof DamagingAbility) {
				info+= "		-Type: DamagingAbility"  + "\n";
				info+= "		-Damage Ammount: " + ((DamagingAbility)a).getDamageAmount()  + "\n";
				
			}else if(a instanceof HealingAbility) {
				info+= "		-Type: HealingAbility"  + "\n";
				info+= "		-Heal Ammount: "+((HealingAbility) a).getHealAmount() + "\n";
				
			}
			c++;
		}
		return info;
	}
	
}
