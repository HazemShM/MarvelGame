package views;

import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.world.Champion;

public class choosechamps {
Scene stac;	
Start str;	
GUI x;


	
public choosechamps(){
	
	
BorderPane main=new BorderPane();	
VBox names=new VBox();
//GridPane champs=new GridPane();
str=new Start();

names.setSpacing(40);



	
	
	
	
	
	
	
	
	
	
	
	
	
	










main.setRight(names);
//main.setLeft(champs);
stac=new Scene(main,600,600);
Image image=new Image("marveliano.jpg");
BackgroundImage ff=new BackgroundImage(image,BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
 Background bGround = new Background(ff);
 main.setBackground(bGround);
	
}	



	
	
	
	
	
	
	
	
	
	

}
