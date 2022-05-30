package views;
import java.io.IOException;
import java.util.*;

import engine.Game;
import engine.Player;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
public class Controller   {
static GUI x;
static Game game;
static Player first;
static Player second;
static Stage stage;







	
	public static void startchamps(String s1, String s2) throws IOException  {
		first=new Player(s1);
		second=new Player(s2);
		game=new Game(first,second);
		game.loadChampions("Champions.csv");
		game.loadAbilities("Abilities.csv");
	
		
	}
		
		
		
		
		
		
		
		
	






	
	
	
	
	
	

	

}
