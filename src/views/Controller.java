package views;

import java.io.IOException;

import java.util.*;
import engine.Game;
import engine.Player;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;

import views.Main;


public class Controller {
	
	Game game;
	Player PlayerOne;
	Player PlayerTwo;


	public Controller(String s1, String s2) throws IOException {
		PlayerOne = new Player(s1);
		PlayerTwo = new Player(s2);
		game = new Game(PlayerOne, PlayerTwo);
		game.loadAbilities("Abilities.csv");
		game.loadChampions("Champions.csv");
		

	}

}
