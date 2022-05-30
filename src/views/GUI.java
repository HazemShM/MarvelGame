package views;



import engine.Game;
import engine.Player;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class GUI extends Application   {
	Game game;
	Scene StartScene;

	Scene beg;
Begin begin;
choosechamps cham;
Scene choosechampss;
	
Start start;
 


	@Override
	public void start(Stage Stage) {
	begin=new Begin();
	beg=begin.bg;
	begin.beg.setOnAction(e -> Stage.setScene(StartScene) );
	begin.ex.setOnAction(e -> Stage.close());
	
		
		
		start=new Start();
	 StartScene=start.st;
	
		
		
		
		
		
		
		
		
	
		
		
		
		
	Stage.setTitle("Marveliano");	
	Stage.getIcons().add(new Image("marvellogo.png"));
		Stage.setScene(beg);
	Stage.show();

	}
	
	public static void main(String[] args) {
		launch(args);
		
	}

	
}
