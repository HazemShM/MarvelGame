package views;

import java.io.IOException;


import java.util.*;
import engine.Game;
import engine.Player;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Window;

import views.Main;

public class Controller {

	Game game;
	Player PlayerOne;
	Player PlayerTwo;
	static Player currentPlayer;


	public Controller(String s1, String s2) throws IOException {

		PlayerOne = new Player(s1);
		PlayerTwo = new Player(s2);
	
		Game.loadAbilities("Abilities.csv");
		Game.loadChampions("Champions.csv");

	}

	public static void control() {
		
		PlayersNames.playersNamesScene();
		PlayersNames.start.setOnMouseClicked(e -> controlHelper());
		

	}

	public static void controlHelper() {
		if (!PlayersNames.p1.getText().equals("") && !PlayersNames.p2.getText().equals("")) {
			try {
				PlayersNames.controller = new Controller(PlayersNames.p1.getText(), PlayersNames.p2.getText());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Controller.currentPlayer = PlayersNames.controller.PlayerOne;
			chooseChampions.chooseChampionsScene();
			
			leaderScene.nextButton = new StyledButton("Next",1);
			GridPane.setConstraints(leaderScene.nextButton.stack,0,1,3,3);
			leaderScene.nextButton.setDisable(true);
			
			leaderScene.nextButton.setOnMouseClicked ( e ->{
	
				leaderScene.leaderWindow.close();
				if(Controller.currentPlayer == PlayersNames.controller.PlayerOne) {
					PlayersNames.controller.PlayerOne.setLeader(leaderScene.leader.champion);
					Controller.currentPlayer = PlayersNames.controller.PlayerTwo;
					leaderScene.nextButton.setDisable(true);
					chooseChampions.chooseChampionsScene();	
				}else{
					PlayersNames.controller.PlayerTwo.setLeader(leaderScene.leader.champion);
					PlayersNames.controller.game= new Game(PlayersNames.controller.PlayerOne, PlayersNames.controller.PlayerTwo);
					GameBoard gameBoard = new GameBoard();
					gameBoard.GameScene();
				}

			});
			

	}
	
	}}
