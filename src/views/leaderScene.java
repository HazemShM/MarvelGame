package views;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.world.Champion;

public class leaderScene {
	
	static StyledButton nextButton;
	static leaderScene leader;
	static Boolean leaderPressed; 
	static Stage leaderWindow ;
	
	ImageView newButton;
	Champion champion;


	public leaderScene(ChampionButton button) {
		champion = button.champion;

		String name = "/resources/ChooseButtons/"+champion.getName()+".png";
		
		newButton = new ImageView(new Image(name));
		newButton.setFitHeight(170);
		newButton.setFitWidth(170);
		newButton.setPreserveRatio(true);

		ColorAdjust c = new ColorAdjust(); // creating the instance of the ColorAdjust effect.   
        c.setBrightness(0.5); // setting the brightness of the color.   
        
		newButton.setOnMouseClicked(e ->{
			if(!leaderPressed) {
				newButton.setEffect(c);
				leaderPressed=true;
				leader = this;
				nextButton.setDisable(false);
				
			}else if (leaderPressed) {
				if(!this.champion.equals(leader.champion)) {
					leader.newButton.setEffect(null);
					newButton.setEffect(c);
					leader = this;
				}
				
				
			}
			
		});
		
	}
	public static void leader() {
		leaderWindow = new Stage();
		leaderWindow.initModality(Modality.APPLICATION_MODAL);
		leaderWindow.setTitle("Choose Your Leader");
		leaderWindow.initOwner(Main.Stage);
		leaderWindow.initStyle(StageStyle.UNDECORATED);
		//leaderWindow.setOnCloseRequest(e->chooseChampions.opaqueLayer.setVisible(false));
		
	
		leaderWindow.initStyle(StageStyle.TRANSPARENT);
		Label name = new Label(Controller.currentPlayer.getName());
		
		name.setStyle("-fx-font: 24 arial;");;
		name.setTextFill(Color.WHITE);
		
		//name.setAlignment(Pos.TOP_LEFT);
		//GridPane.setConstraints(name, 1, 0,2,1);
		
		GridPane leaderGrid = new GridPane();
		//leaderGrid.setMaxSize(2, 1);
		//leaderGrid.autosize();
		//leaderGrid.setPadding(new Insets(10,10,10,10));
		leaderGrid.setVgap(20);
		leaderGrid.setHgap(8);
		//leaderGrid.getChildren().add(name);
		ImageView imageview = new ImageView("/resources/Leader.png");
		
		
		
        StackPane leaderPane =new StackPane();
        leaderPane.getChildren().addAll(imageview,leaderGrid);
        leaderGrid.setAlignment(Pos.CENTER);
        leaderPane.setStyle("-fx-background-color: transparent;");
		Scene scene =new Scene(leaderPane,800,600);
		scene.setFill(Color.TRANSPARENT);
		imageview.setFitHeight(450);
		imageview.setFitWidth(650);
        
		leaderWindow.setScene(scene);
		int x=0;

		for (ChampionButton button : chooseChampions.buttons) {
			if(button.pressed) {
				leaderScene b = new leaderScene (button);
				leaderGrid.add(b.newButton , x, 1);
				x++;
			}
		}
		leaderGrid.getChildren().add(nextButton.stack);
		
		leaderWindow.showAndWait();
		chooseChampions.opaqueLayer.setVisible(false);
		chooseChampions.main.setEffect(null);
		
	}
	
	
}
