package views;

import javafx.event.ActionEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.*;
import javafx.stage.Popup;
import model.abilities.*;
import model.world.Champion;
import model.world.Hero;
import model.world.Villain;

public class ChampionButton {
	ImageView championButton;
	Champion champion;
	Boolean pressed; 
	Image img;

	public ChampionButton(Champion c) {
		champion =c;
		//championButton.setMaxSize(100, 100);
		//championButton.setStyle("-fx-base: coral;");

		String name = "/resources/ChooseButtons/"+c.getName()+".png";
		img = new Image(name);
		championButton = new ImageView(img);
		championButton.setFitHeight(150);
		championButton.setFitWidth(150);
		championButton.setPreserveRatio(true);
		//championButton.setContentDisplay(ContentDisplay.TOP);
		
		//championButton.setStyle("-fx-background-color: White; ");
		//InnerShadow is = new InnerShadow();

		//championButton.setTextFill(Color.BLACK);
		//championButton.setFont(Font.font(null, FontWeight.BOLD, 15));
		pressed = false;

		//DropShadow dropShadow = new DropShadow();
		//InnerShadow innerShadow = new InnerShadow();
//
//		Popup popup = new Popup();
//		Label label = new Label();
//		
//		Tooltip tooltip = new Tooltip();
//		VBox v = new VBox();
//		Label l= new Label();
//		l.setText("Hellloooo");
//		v.getChildren().add(championButton);
//		label.setPrefSize(100, 300);
//		label.setTextFill(Color.color(1, 0, 0));
//		tooltip.setGraphic(new ImageView(img));
//		Tooltip.install(championButton,tooltip);

		championButton.setOnMouseClicked(e ->handle());
		
	}
	public void handle() {
		chooseChampions.championDetails.getChildren().remove(chooseChampions.label);
		chooseChampions.label = new Label();
		getInfo();
		
		chooseChampions.championDetails.getChildren().addAll( chooseChampions.label);
		chooseChampions.championDetails.setPadding(new Insets(0, 50, 0, 0));
		ColorAdjust c = new ColorAdjust(); // creating the instance of the ColorAdjust effect.   
        c.setBrightness(0); // setting the brightness of the color.   
        //c.setContrast(0.1); // setting the contrast of the color  
        c.setHue(0.6); // setting the hue of the color  
        //c.setSaturation(0.45); // setting the hue of the color.   
        
		if (!pressed && chooseChampions.numberOfChampions <=2) {
			
			
			pressed = true;
			chooseChampions.numberOfChampions++;
			Controller.currentPlayer.getTeam().add(champion);
			
			championButton.setEffect(c);
			if (chooseChampions.numberOfChampions ==3) {
				chooseChampions.chooseLeaderButton.setDisable(false);
			}

		}else if(pressed) {
			if (chooseChampions.numberOfChampions ==3) {
				
				chooseChampions.chooseLeaderButton.setDisable(true);
			}
				
			championButton.setEffect(null);
			pressed = false;
			chooseChampions.numberOfChampions--;
			Controller.currentPlayer.getTeam().remove(champion);
			
			
		}
		
	}
	public void place(int x , int y) {
		GridPane.setConstraints(championButton,x,y);
	}
	public void getInfo() {

		chooseChampions.championDetails.getChildren().clear();
		chooseChampions.abilitiesDetails.getChildren().clear();
		
		Label name= new Label(champion.getName());

		//name.setStyle("-fx-font: 30 arial;");;
		name.setFont(Font.font("Verdana", FontWeight.BOLD, 27));
		name.setTextFill(Color.YELLOW);
		name.setAlignment(Pos.CENTER);
		//name.setTextAlignment(TextAlignment.LEFT);
		
		Label abilityLabel = new Label(champion.getName()+" Abilities: ");
		abilityLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 21));
		abilityLabel.setTextFill(Color.YELLOW);
		abilityLabel.setAlignment(Pos.CENTER);
		//abilityLabel.setTextAlignment(TextAlignment.LEFT);
		
		
		Label l2;
		Label l3;
		
		
		String champ = "";
		String ability = "";
		
		if(champion instanceof Hero) {
			champ += "Type: Hero" + "\n";
		}else if (champion instanceof Villain) {
			champ += "Type: Villain" + "\n";
		}else {
			champ += "Type: AntiHero" + "\n";
		}
		champ += "Max HP: " + champion.getMaxHP() + "\n";
		champ += "Mana: " + champion.getMana()  + "\n";
		champ += "Max ActionPoints: " + champion.getMaxActionPointsPerTurn() + "\n";
		champ += "Speed: " + champion.getSpeed()  + "\n";
		champ += "Attack Range: " +champion.getAttackRange()  + "\n";
		champ += "Attack Damage: " +champion.getAttackDamage() + "\n";
		champ +=   "\n";
		
		int c=1;
		for (Ability a : champion.getAbilities()) {
			ability+= "Ability " +  a.getName() +":"  + "\n";
			ability+= "	-Mana Cost: " + a.getManaCost()  + "\n";
			ability+= "	-CoolDown: " +a.getBaseCooldown() + "\n";
			ability+= "	-Range: " +a.getCastRange() + "\n";
			ability+= "	-AOE: "+a.getCastArea() + "\n";
			ability+= "	-AtionPoints: "+a.getRequiredActionPoints() + "\n";
		
			if(a instanceof CrowdControlAbility) {
				ability+= "	-Type: CrowdControl"  + "\n";
				ability+= "	-Effect: "+  "\n";
				ability+= "		Name: "+ ((CrowdControlAbility)a).getEffect().getName() + "\n";
				ability+= "		Duration: " +((CrowdControlAbility)a).getEffect().getDuration() + "\n";
				ability+= "		Type: "+((CrowdControlAbility)a).getEffect().getType()  + "\n";

			}else if(a instanceof DamagingAbility) {
				ability+= "	-Type: Damaging"  + "\n";
				ability+= "	-Damage Amount: " + ((DamagingAbility)a).getDamageAmount()  + "\n";
				
			}else if(a instanceof HealingAbility) {
				ability+= "	-Type: Healing"  + "\n";
				ability+= "	-Heal Amount: "+((HealingAbility) a).getHealAmount() + "\n";
				
			}
			c++;
		}
		Label champText = new Label(champ);
		Label abilityText = new Label(ability);

		champText.setAlignment(Pos.CENTER_LEFT);
		abilityText.setAlignment(Pos.CENTER_LEFT);
		
		ImageView champImage = new ImageView(new Image("/resources/Champions/"+champion.getName()+".png"));
		if(champion.getName().equals("Iceman"))
			champImage.setFitHeight(230);
		else
			champImage.setFitHeight(300);
		
		
		champImage.setPreserveRatio(true);
		
		champText.setStyle("-fx-font: 20 arial;");;
		champText.setTextFill(Color.WHITE);
		
		
		abilityText.setStyle("-fx-font: 20 arial;");;
		abilityText.setTextFill(Color.WHITE);
		
		
		chooseChampions.championDetails.getChildren().addAll(name,champText , champImage);
		chooseChampions.abilitiesDetails.getChildren().addAll(abilityLabel,abilityText);
		
	}
	
}
