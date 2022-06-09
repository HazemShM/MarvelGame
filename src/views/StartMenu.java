package views;

import java.io.File;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Screen;

public class StartMenu {
	
	static Scene startScene;

	public static void startMenu() {
		
		Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
		Label label = new Label();
		Image img = new Image("/resources/logo.png");
		ImageView view = new ImageView(img);
	    
	    view.setPreserveRatio(true);
	    label.setGraphic(view);
	    label.setGraphic(null);
		label.setAlignment(Pos.TOP_CENTER);
		label.setMaxSize(400, 400);


		VBox b = new VBox();
		startScene = new Scene(b, screenSize.getWidth(), screenSize.getHeight());
//		System.out.println(screenSize.getWidth());
//		System.out.println(screenSize.getHeight());
		b.setStyle(
	            "-fx-background-image: url(" +
	                "/resources/marvel3.jpg" +
	            "); " +
	            "-fx-background-size: cover;"
	        );
//		startButton = new Button();
//		startButton.setStyle("-fx-background-color: transparent;");
//		startButton.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));
//		startButton.setOnAction(e -> Controller.control());
		

		b.setAlignment(Pos.CENTER);
		b.setSpacing(50);
		
		Image image = new Image("/resources/marvel3.jpg");

//		BackgroundImage ff = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
//				BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
//		
//		Background bGround = new Background(ff);
//		
//		b.setBackground(bGround);
		StyledButton startButton = new StyledButton("START",1);
		startButton.setOnAction( e-> Controller.control());
		
		StyledButton exitButton = new StyledButton("EXIT",1);
		exitButton.setOnAction(e-> Main.Stage.close());
		
		
		b.getChildren().addAll(label, startButton.stack , exitButton.stack);
		
		Main.Stage.setScene(startScene);
	}

}
