package views;

import java.io.FileInputStream;

import engine.Player;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Winner {
	public Winner(Player p) {
	
		StackPane champion=new StackPane();

		Label label = new Label();
		Image img = new Image( "/resources/finalscene.jpg");
		ImageView view = new ImageView(img);
		view.fitWidthProperty().bind(StartMenu.startScene.widthProperty());
        view.fitHeightProperty().bind(StartMenu.startScene.heightProperty());
	    label.setText(p.getName());
	    view.setPreserveRatio(true);
	   
		
		
		//FileInputStream inputstream = new FileInputStream("/resources/crown.gif");
		//Image image = new Image(inputstream);
		//ImageView crown = new ImageView(image);


		label.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 50));
		

		label.setAlignment(Pos.CENTER);
		label.setText("The Winner is "+ p.getName());
		label.setMinWidth(300);
		label.setTextFill(Color.AQUA);
		VBox x=new VBox();
		x.setSpacing(100);
		x.setAlignment(Pos.CENTER);
		
		

		ImageView champImage = new ImageView(new Image("/resources/Champions/"+p.getLeader().getName()+".png"));
		champImage.setFitHeight(300);
		champImage.setPreserveRatio(true);
	
	
		
		BackgroundImage ff = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
			BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		Background bGround = new Background(ff);
		 

		champion.setBackground(bGround);

		x.getChildren().addAll(label,champImage);
		champion.getChildren().add(x);

		Main.swapScenes(champion);
	}
}