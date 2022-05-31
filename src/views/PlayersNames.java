package views;

import java.io.IOException;


import engine.Game;
import engine.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import views.Main;

public class PlayersNames {

	static Button start;
	static TextField p1,p2;
	static Scene playersNames;
	public static Controller controller;
	
	public static void playersNamesScene() {

		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);
		playersNames = new Scene(root, 1000, 600, Color.BEIGE);
		start = new Button("start");
		start.setFont(Font.font("Aguda", 15));

		p1 = new TextField();
		p1.setPromptText("please enter your name");
		p2 = new TextField();
		p1.setAlignment(Pos.CENTER);
		Label f = new Label("Player 1");
		Label s = new Label("Player 2");
		
		p2.setAlignment(Pos.CENTER);
		p2.setPromptText("please enter your name");
		
		p1.setText("Player one");
		p2.setText("Player two");
		
		p1.setMaxWidth(400);
		p1.setMaxHeight(400);
		p2.setMaxHeight(400);
		p2.setMaxWidth(400);
		root.setSpacing(30);
		start.setMaxSize(200, 100);
		p1.setFont(Font.font("Aguda", 15));
		p2.setFont(Font.font("Aguda", 15));
		f.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));
		s.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));
		
		Image image = new Image("/resources/marvel3.jpg");
		BackgroundImage ff = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		Background bGround = new Background(ff);
		root.setBackground(bGround);
		f.setTextFill(Color.RED);
		s.setTextFill(Color.RED);
		start.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));

		root.getChildren().addAll(f, p1, s, p2, start);
		Main.Stage.setScene(playersNames);
	}
	

	
}





