package views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Begin {
	Button beg,ex;
	Scene bg;
	public Begin() {
	Label welc=new Label("Marveliano");	
	welc.setAlignment(Pos.TOP_CENTER);
	welc.setMaxSize(300,300);
	welc.setTextFill(Color.RED);
	 welc.setFont(Font.font("Comic Sans MS",FontWeight.EXTRA_BOLD,45));

	
	
	
	VBox b=new VBox();	
	 bg=new Scene(b,800,800);
	 beg=new Button("Start");
	 beg.setFont(Font.font("Aguda",FontWeight.EXTRA_BOLD,35));
	
	 ex=new Button("Quit");	
	 ex.setFont(Font.font("Aguda",FontWeight.EXTRA_BOLD,35));
	 
	 b.setAlignment(Pos.CENTER);
	 b.setSpacing(50);
	 beg.setMaxSize(200, 100);
	 ex.setMaxSize(200, 100);
	 
	 
	 Image image=new Image("marveliano.jpg");
		BackgroundImage ff=new BackgroundImage(image,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
		 Background bGround = new Background(ff);
		 b.setBackground(bGround); 
	 
	 
	 
	 
	 
		b.getChildren().addAll(welc,beg,ex);
		
		
		
	}

}
