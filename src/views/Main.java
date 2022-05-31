package views;


import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;



public class Main extends Application {
	
	public static Stage Stage;
	
	@Override
	public void start(Stage s) {
		Stage = s;
		
		Stage.setTitle("Marveliano");
		Stage.getIcons().add(new Image("/resources/marvellogo.png"));

		StartMenu.startMenu();
		
		Stage.show();

	}

	public static void main(String[] args) {
		launch(args);

	}

}
