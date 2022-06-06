package views;

import javafx.application.Application;
import javafx.stage.Stage;
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

		StartMenu.startMenu();
	
		Stage.show();

	}

	public static void main(String[] args) {
		launch(args);

	}

}
