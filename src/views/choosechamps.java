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

public class choosechamps {
	
	static Scene ChampionsScene;
	static ArrayList<ChampionButton> buttons;
	static BorderPane main;
	static GridPane grid;
	static VBox Choosen; 
	static Label label ;
	static ImageView image ;
	static int numberOfChampions;
	static Player player ;
	static int whichPlayer;
	public static void Choose(Player p) {
		player =p;
		main = new BorderPane();
		Choosen = new VBox();
		grid = new GridPane();
		

		Label nameLabel = new Label("Player: "+p.getName());
		nameLabel.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));
		nameLabel.setTranslateX(50);
		
		ChampionsScene = new Scene(main, 1000, 700, Color.BEIGE);
		numberOfChampions =0;
		Choosen.setSpacing(40);
		
		main.setRight(Choosen);
		main.setLeft(grid);
		main.setTop(nameLabel);
		
		grid.setMaxSize(4, 4);
		grid.autosize();
		grid.setPadding(new Insets(10,10,10,10));
		grid.setVgap(8);
		grid.setHgap(8);
		
		
		int y = 0;
		int x = 0;
		buttons= new ArrayList<>();
		for(Champion c : Game.getAvailableChampions()) {
			ChampionButton b = new ChampionButton(c);
			b.place(y,x);
			if(x<3 && y<4) x++;
			else if(x==3 && y<3) {
				x=0;
				y++;
			}
			buttons.add(b);
			grid.getChildren().add(b.championButton);
		}
		if(whichPlayer==2) {
			for(ChampionButton button : buttons) {
				if(PlayersNames.controller.PlayerOne.getTeam().contains(button.champion))
					button.championButton.setDisable(true);
			}
		}
		
		
		Main.Stage.setScene(ChampionsScene);
	}

}
