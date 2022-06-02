package views;

import java.util.ArrayList;


import engine.Game;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.world.*;

public class PreGame {

	static Label[][] labels;
	static GridPane gameGrid;
	static boolean space , q,w,e,r;
	public static void preGameScene() {

		BorderPane main = new BorderPane();

		VBox left = new VBox();
		VBox right = new VBox();
		gameGrid = new GridPane();
		HBox bottom = new HBox();

		labels = new Label[Game.getBoardwidth()][Game.getBoardheight()];
		Scene preGameScene = new Scene(main, 1000, 800, Color.BEIGE);

		left.setSpacing(40);

		main.setCenter(gameGrid);
		gameGrid.setPrefHeight(500);
		gameGrid.setPrefWidth(500);
		
		
        final int numCols = 5 ;
        final int numRows = 5 ;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(100);
            gameGrid.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(100);;
            gameGrid.getRowConstraints().add(rowConst);         
        }
		
		String name = "/resources/rockView.jpg";
		Image img = new Image(name);
	
		gameGrid.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

		Main.Stage.setScene(preGameScene);

		reloadBoard();
		Game game = PlayersNames.controller.game;
		System.out.println(game.getCurrentChampion().getName());
		
		preGameScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			
			@Override
			public void handle(KeyEvent event) {
				
				switch (event.getCode()) {
				case R: break;
				case SPACE: space=true;
				case Q: q=true;
				case W: w=true;
				case E: e=true;
				
				case UP, DOWN, LEFT, RIGHT:
					move(event.getCode());
					break;
				
				}

			}

		});
	}

	public static void move(KeyCode d) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();
		Game game = PlayersNames.controller.game;
		Direction direction ;
		
		if(d==KeyCode.UP) {
			direction = Direction.DOWN;
		}else if(d==KeyCode.DOWN) {
			direction = Direction.UP;
		}else if (d==KeyCode.LEFT){
			direction = Direction.LEFT;
		}else {
			direction = Direction.RIGHT;
		}
		try {
			int x = c.getLocation().x;
			int y = c.getLocation().y;
			game.move(direction);
			GridPane.setConstraints(labels[x][y],c.getLocation().y,c.getLocation().x);
			labels[c.getLocation().x][c.getLocation().y]=labels[x][y];
			labels[x][y] = null;	
		} catch (NotEnoughResourcesException | UnallowedMovementException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
		}
		
		
	}

	public static void checkIfDead() {
		Object[][] board = PlayersNames.controller.game.getBoard();
		for (int i = 0; i < Game.getBoardheight(); i++) {
			for (int j = 0; j < Game.getBoardwidth(); j++) {
				if (board[i][j] == null && labels[i][j].getId() != null) {
					labels[i][j].setId(null);
					labels[i][j].setGraphic(null);
				}
			}
		}
	}

	public static void reloadBoard() {
		Game game = PlayersNames.controller.game;
		
		for (int i = 0; i < Game.getBoardheight(); i++) {
			for (int j = 0; j < Game.getBoardwidth(); j++) {
				Label label = new Label();
				label.setMinSize(150, 150);
				if (game.getBoard()[i][j] != null) {
					if (game.getBoard()[i][j] instanceof Cover) { 
						label.setId("Cover");
						String name = "/resources/cover100.png";
						Image img = new Image(name);
						ImageView view = new ImageView(img);
						view.setPreserveRatio(true);
						label.setGraphic(view);
					} else if (game.getBoard()[i][j] instanceof Champion) {
						label.setId(((Champion) game.getBoard()[i][j]).getName());
						String name = "/resources/100x100/" + ((Champion) game.getBoard()[i][j]).getName() + "2.png";
						Image img = new Image(name);
						ImageView view = new ImageView(img);
						view.setPreserveRatio(true);
						label.setGraphic(view);

					}
					GridPane.setConstraints(label, j, i);
					labels[i][j] = label;
					gameGrid.getChildren().add(label);
				}
				
			}
		}
	}

}
