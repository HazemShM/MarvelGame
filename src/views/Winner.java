package views;

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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Winner {
	public Winner(Player p) {
		VBox box = new VBox();

		Label label = new Label();
		//Image img = new Image("/resources/logo.png");
		//ImageView view = new ImageView(img);
	    label.setText(p.getName());
	   // view.setPreserveRatio(true);
	   
		
		label.setMaxSize(400, 400);

		
		VBox b = new VBox();
		Scene winnerScene = new Scene(b, 1000, 600);

		label.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));

		label.setAlignment(Pos.CENTER);
		
		//Image image = new Image("/resources/marvel3.jpg");
		//BackgroundImage ff = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
		//		BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		//Background bGround = new Background(ff);
		//b.setBackground(bGround);

		b.getChildren().addAll(label);

		Main.swapScenes(b);
	}
}
