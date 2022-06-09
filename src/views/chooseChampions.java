package views;

import java.io.IOException;
import java.util.ArrayList;

import engine.Game;
import engine.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import model.world.Champion;

public class chooseChampions {
	
	//static Scene ChampionsScene;
	static ArrayList<ChampionButton> buttons;
	static BorderPane main;
	static GridPane grid;
	static VBox championDetails; 
	static VBox abilitiesDetails;
	static Label label ;
	static ImageView image ;
	static int numberOfChampions;
	
	
	
	static StyledButton chooseLeaderButton;

	static StyledButton next;
	
	public  void chooseChampionsScene() {

		Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();

		
		main = new BorderPane();
		StackPane root = new StackPane(main,opaqueLayer);   
		Main.swapScenes(root);
		
		championDetails = new VBox();
		abilitiesDetails = new VBox();
		
		
		grid = new GridPane();
		
		ScrollPane scroll = new ScrollPane();
		scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scroll.setContent(abilitiesDetails);
		scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; "); 
		
//		Label nameLabel = new Label("Player: "+ Controller.currentPlayer.getName());
//		nameLabel.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 25));
//		nameLabel.setTranslateX(50);
		StackPane playerName = new StackPane();
		Image pImg;
		//if(Controller.currentPlayer.equals(PlayersNames.controller.PlayerOne)) {
		//	pImg = new Image("/resources/PlayerName.png");
		//}else {
			pImg = new Image("/resources/PlayerName2.png");
		//}
		ImageView player = new ImageView(pImg);
		player.setFitHeight(300);
		player.setFitWidth(500);
		player.setPreserveRatio(true);
		Text name = new Text(Controller.currentPlayer.getName());
		String FONT_PATH = "/resources/fonts/Fighting_Spirit_2_bold.ttf";
		name.setFont(Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 35));
		name.setFill(Color.WHITE);
		name.setTextAlignment(TextAlignment.CENTER);
		playerName.getChildren().addAll(player,name);
		
		//ChampionsScene = new Scene(main,screenSize.getWidth(), screenSize.getHeight());
		
		
        
		numberOfChampions =0;

		ImageView imageview = new ImageView("/resources/ChooseChampions.jpg");
		BoxBlur bb = new BoxBlur();
		imageview.setEffect(bb);
		bb.setIterations(3);
		
        imageview.fitWidthProperty().bind(StartMenu.startScene.widthProperty());
        imageview.fitHeightProperty().bind(StartMenu.startScene.heightProperty());
        
        GridPane center =  new GridPane();
        center.setAlignment(Pos.CENTER);
        
        final int numCols = 3;
		final int numRows = 1;
		for (int i = 0; i < numCols; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			if(i==0 || i==2)
				colConst.setPrefWidth(320);

			center.getColumnConstraints().add(colConst);
		}
		for (int i = 0; i < numRows; i++) {
			RowConstraints rowConst = new RowConstraints();
			//rowConst.setPrefHeight(100);

			center.getRowConstraints().add(rowConst);
		}
		center.setHgap(30);
        
        GridPane.setConstraints(grid, 1, 0);
        GridPane.setConstraints(championDetails, 0, 0);
        GridPane.setConstraints(scroll, 2, 0);
        center.getChildren().addAll(grid,championDetails,scroll);
        main.getChildren().addAll(imageview);
		//main.setRight(Choosen);
		main.setCenter(center);
		main.setTop(playerName);
//		main.setLeft(championDetails);
//		main.setRight(abilitiesDetails);
		championDetails.setAlignment(Pos.TOP_CENTER);
		abilitiesDetails.setAlignment(Pos.TOP_CENTER);
		//championDetails.setLayoutX(10);
		main.setPadding(new Insets(20,20,20,20));
		//grid.setMaxSize(4, 2);
		//grid.autosize();
		//grid.setPadding(new Insets(10,10,10,10));
		grid.setVgap(8);
		grid.setHgap(8);
		grid.setAlignment(Pos.CENTER);
		//4x4 -> 5x3
		int y = 0;
		int x = 0;
		buttons= new ArrayList<>();
		for(Champion c : Game.getAvailableChampions()) {
			ChampionButton b = new ChampionButton(c);
			b.place(x,y);
			if(x<4 && y<3) x++;
			else if(x==4 && y<2) {
				x=0;
				y++;
			}
			buttons.add(b);
			grid.getChildren().add(b.championButton);
		}
		if(Controller.currentPlayer.equals(PlayersNames.controller.PlayerTwo)) {
			for(ChampionButton button : chooseChampions.buttons) {
				if(PlayersNames.controller.PlayerOne.getTeam().contains(button.champion)) {
					BoxBlur b = new BoxBlur();
					button.championButton.setEffect(b);
					b.setIterations(15);
					
					button.championButton.setDisable(true);
				}
					
			}
		}
		chooseLeaderButton = new StyledButton("Set Leader",3);
		
		
		
		chooseLeaderButton.stack.setAlignment(Pos.CENTER);
		
		GridPane.setConstraints(chooseLeaderButton.stack,1,3,3,1);
		
		
		leaderScene.leaderPressed=false;

      
       
		
        opaqueLayer.setVisible(false);
        opaqueLayer.setStyle("-fx-background-color: #00000044;");
        opaqueLayer.resizeRelocate(0, 0, StartMenu.startScene.getWidth(), StartMenu.startScene.getHeight());
		chooseChampions.chooseLeaderButton.setOnAction(e ->{
			opaqueLayer.setVisible(true);
			main.setEffect(bb);
			leaderScene.leader();
			});
		grid.getChildren().add(chooseLeaderButton.stack);
		chooseLeaderButton.setDisable(true);
		//Main.Stage.setScene(ChampionsScene);
		//Main.swapScenes(main);
		
	}
	 final static Region opaqueLayer = new Region();

}
