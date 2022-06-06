package views;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import model.abilities.*;
import model.world.Champion;

public class ChampionButton {
	Button championButton;
	Champion champion;
	Boolean pressed; 
	Image img;

	public ChampionButton(Champion c) {
		champion =c;
		championButton = new Button(c.getName());
		championButton.setMaxSize(150, 150);
		//championButton.setStyle("-fx-base: coral;");

		String name = "/resources/"+c.getName()+".png";
		img = new Image(name);
		ImageView view = new ImageView(img);
		championButton.setGraphic(view);
		championButton.setContentDisplay(ContentDisplay.TOP);
		
		championButton.setStyle("-fx-background-color: White; ");
		InnerShadow is = new InnerShadow();

		championButton.setEffect(is);
		championButton.setTextFill(Color.BLACK);
		championButton.setFont(Font.font(null, FontWeight.BOLD, 15));
		pressed = false;

		championButton.setOnAction(e ->handle());
		
	}
	public void handle() {
		chooseChampions.Choosen.getChildren().remove(chooseChampions.label);
		chooseChampions.label = new Label();
		chooseChampions.label.setText(getInfo());
		
		chooseChampions.Choosen.getChildren().addAll( chooseChampions.label);
		chooseChampions.Choosen.setPadding(new Insets(0, 50, 0, 0));
		
		if (!pressed && chooseChampions.numberOfChampions <=2) {
			
			championButton.setStyle("-fx-background-color: Green; ");
			pressed = true;
			chooseChampions.numberOfChampions++;
			Controller.currentPlayer.getTeam().add(champion);
			
			if (chooseChampions.numberOfChampions ==3) {
				chooseChampions.chooseLeaderButton.setDisable(false);
			}

		}else if(pressed) {
			if (chooseChampions.numberOfChampions ==3) {
				
				chooseChampions.chooseLeaderButton.setDisable(true);
			}
				
			championButton.setStyle("-fx-background-color: White; ");
			pressed = false;
			chooseChampions.numberOfChampions--;
			Controller.currentPlayer.getTeam().remove(champion);
			
			
		}
		
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
				info+= "		-Effect: Name: "+ ((CrowdControlAbility)a).getEffect().getName() + "\n";
				info+= "				 Duration: " +((CrowdControlAbility)a).getEffect().getDuration() + "\n";
				info+= "				 EffectType: "+((CrowdControlAbility)a).getEffect().getType()  + "\n";

			}else if(a instanceof DamagingAbility) {
				info+= "		-Type: DamagingAbility"  + "\n";
				info+= "		-Damage Amount: " + ((DamagingAbility)a).getDamageAmount()  + "\n";
				
			}else if(a instanceof HealingAbility) {
				info+= "		-Type: HealingAbility"  + "\n";
				info+= "		-Heal Amount: "+((HealingAbility) a).getHealAmount() + "\n";
				
			}
			c++;
		}
		return info;
	}
	
}
