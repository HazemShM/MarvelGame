package views;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.image.Image;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;



public class Main extends Application {
	
	public static Stage Stage;
	public static MediaPlayer mediaPlayer;
	@Override
	public void start(Stage s) {
		
		Media sound = new Media(getClass().getResource("/resources/sound2.mpeg").toExternalForm());
		mediaPlayer = new MediaPlayer(sound);
		mediaPlayer.setAutoPlay(true);
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
		mediaPlayer.play();
		
		Stage = s;
		Stage.setResizable(false);
		
		Stage.setTitle("Marveliano");
		Stage.getIcons().add(new Image("/resources/marvellogo.png"));

		Stage.setFullScreenExitHint("");
		Stage.setFullScreen(true);
		Stage.setMaximized(true);
		StartMenu.startMenu();
		
		Stage.show();
	
		
//		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
//        Stage.setX((primScreenBounds.getWidth() - Stage.getWidth()) / 2);
//        Stage.setY((primScreenBounds.getHeight() - Stage.getHeight()) / 2);
	}
	public static void swapScenes(Parent newContent){
	
	    Stage.getScene().setRoot(newContent);
	}
	public static void main(String[] args) {
		launch(args);

	}

}
