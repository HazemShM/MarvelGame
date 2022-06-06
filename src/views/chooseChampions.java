package views;

import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import engine.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.world.Champion;

public class chooseChampions {
	
	static Scene ChampionsScene;
	static ArrayList<ChampionButton> buttons;
	static BorderPane main;
	static GridPane grid;
	static VBox Choosen; 
	static Label label ;
	static ImageView image ;
	static int numberOfChampions;
	
	
	
	static StyledButton chooseLeaderButton;

	static StyledButton next;
	
	public static void chooseChampionsScene() {
		main = new BorderPane();
		Choosen = new VBox();
		grid = new GridPane();
		HBox hbox = new HBox();
		


		Label nameLabel = new Label("Player: "+ Controller.currentPlayer.getName());
		nameLabel.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 25));
		nameLabel.setTranslateX(50);
		
		ChampionsScene = new Scene(main, 1200, 720, Color.BEIGE);
		numberOfChampions =0;
		Choosen.setSpacing(40);
		
		main.setRight(Choosen);
		main.setLeft(grid);
		main.setTop(nameLabel);
		main.setBottom(hbox);
		
		grid.setMaxSize(4, 2);
		grid.autosize();
		grid.setPadding(new Insets(10,10,10,10));
		grid.setVgap(8);
		grid.setHgap(8);
		
		//4x4 -> 5x3
		int y = 0;
		int x = 0;
		buttons= new ArrayList<>();
		for(Champion c : Game.getAvailableChampions()) {
			ChampionButton b = new ChampionButton(c);
			b.place(x,y);
			if(x<4 && y<3) x++;
			else if(x==4 && y<2) {
				x=0;
				y++;
			}
			buttons.add(b);
			grid.getChildren().add(b.championButton);
		}
		if(Controller.currentPlayer.equals(PlayersNames.controller.PlayerTwo)) {
			for(ChampionButton button : chooseChampions.buttons) {
				if(PlayersNames.controller.PlayerOne.getTeam().contains(button.champion))
					button.championButton.setDisable(true);
			}
		}
		chooseLeaderButton = new StyledButton("Set Leader",3);
		
		GridPane.setConstraints(chooseLeaderButton.stack,1,3,3,1);
		
		
		leaderScene.leaderPressed=false;
		chooseChampions.chooseLeaderButton.setOnMouseClicked(e ->leaderScene.leader());
		grid.getChildren().add(chooseLeaderButton.stack);
		chooseLeaderButton.setDisable(true);
		Main.Stage.setScene(ChampionsScene);
	}

}
