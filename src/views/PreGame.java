package views;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PreGame {
	public static void preGameScene() {
	
		BorderPane main = new BorderPane();
		
		VBox Choosen = new VBox();
		GridPane grid = new GridPane();
		HBox hbox = new HBox();
		


		Scene preGameScene = new Scene(main, 1200, 720, Color.BEIGE);

		Choosen.setSpacing(40);
		
		main.setRight(Choosen);
		main.setLeft(grid);
	
		main.setBottom(hbox);
		
		grid.setMaxSize(4, 2);
		grid.autosize();
		grid.setPadding(new Insets(10,10,10,10));
		grid.setVgap(8);
		grid.setHgap(8);
		
		Main.Stage.setScene(preGameScene);
	}
	
}
