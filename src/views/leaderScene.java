package views;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.world.Champion;

public class leaderScene {
	
	static Button nextButton;
	static leaderScene leader;
	static Boolean leaderPressed; 
	static Stage leaderWindow ;
	
	Button newButton;
	Champion champion;


	public leaderScene(ChampionButton button) {
		champion = button.champion;
		
		newButton = new Button(champion.getName());
		newButton.setMaxSize(150, 150);
		String name = "/resources/"+champion.getName()+".png";
		Image img = new Image(name);
		ImageView view = new ImageView(img);
		newButton.setGraphic(view);
		newButton.setContentDisplay(ContentDisplay.TOP);
		
		InnerShadow is = new InnerShadow();
		newButton.setEffect(is);
		newButton.setTextFill(Color.BLACK);
		newButton.setFont(Font.font(null, FontWeight.BOLD, 15));
		
		newButton.setStyle("-fx-background-color: Green; ");
		newButton.setOnAction(e ->{
			if(!leaderPressed) {
				newButton.setStyle("-fx-background-color: Yellow; ");
				leaderPressed=true;
				leader = this;
				nextButton.setDisable(false);
				
			}else if (leaderPressed) {
				if(!this.champion.equals(leader.champion)) {
					leader.newButton.setStyle("-fx-background-color: Green; ");
					newButton.setStyle("-fx-background-color: Yellow; ");
					leader = this;
				}
				
				
			}
			
		});
		
	}
	public static void leader() {
		leaderWindow = new Stage();
		leaderWindow.initModality(Modality.APPLICATION_MODAL);
		leaderWindow.setTitle("Choose Your Leader");
		
		GridPane leaderGrid = new GridPane();
		leaderGrid.setMaxSize(2, 1);
		leaderGrid.autosize();
		leaderGrid.setPadding(new Insets(10,10,10,10));
		leaderGrid.setVgap(8);
		leaderGrid.setHgap(8);
		
		Scene scene =new Scene(leaderGrid);
		leaderWindow.setScene(scene);
		int x=0;

		for (ChampionButton button : chooseChampions.buttons) {
			if(button.pressed) {
				leaderScene b = new leaderScene (button);
				leaderGrid.add( b.newButton , x, 0);
				x++;
			}
		}
		leaderGrid.getChildren().add(nextButton);
		
		leaderWindow.showAndWait();
		
		
	}
	
	
}
