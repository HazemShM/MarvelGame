package views;

import java.util.ArrayList;

import engine.Game;
import engine.Player;
import exceptions.*;
import javafx.animation.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.effect.Light.Distant;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import model.abilities.*;
import model.world.*;

public class GameBoard {

	static Label[][] labels;
	GridPane gameGrid;
	boolean space, q, w, e, singleTarget = false;
	HBox top;
	static BorderPane main;
	StackPane stackImage;
	ProgressBar healthbar;
	LayoutAnimator animator;
	Game game = PlayersNames.controller.game;
	static boolean move = true;
	public void GameScene() {

		main = new BorderPane();

		Main.mediaPlayer.stop();

		gameGrid = new GridPane();

		labels = new Label[Game.getBoardwidth()][Game.getBoardheight()];
		Scene boardScene = new Scene(main, 1200, 720, Color.BEIGE);

		main.setPrefHeight(boardScene.getHeight());
		main.setPrefWidth(boardScene.getWidth());
		gameGrid.setPrefHeight(500);
		gameGrid.setPrefWidth(500);
		
		final int numCols = 5;
		final int numRows = 5;
		for (int i = 0; i < numCols; i++) {
			ColumnConstraints colConst = new ColumnConstraints();
			colConst.setPrefWidth(100);

			gameGrid.getColumnConstraints().add(colConst);
		}
		for (int i = 0; i < numRows; i++) {
			RowConstraints rowConst = new RowConstraints();
			rowConst.setPrefHeight(100);

			gameGrid.getRowConstraints().add(rowConst);
		}

		String name = "/resources/rockView.jpg";
		Image img = new Image(name);
		ImageView backGround = new ImageView(img);

//		backGround.setFitHeight(500);
//		backGround.setFitWidth(500);
		stackImage = new StackPane();
		stackImage.setAlignment(Pos.TOP_CENTER);
		gameGrid.setAlignment(stackImage.getAlignment());
		stackImage.getChildren().addAll(backGround, gameGrid);

		main.setCenter(stackImage);

//		gameGrid.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
//				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

		Main.Stage.setScene(boardScene);
		animator = new LayoutAnimator();
		loadBoard();
		
	    
	  
		StyledButton endTurn = new StyledButton("EndTurn", 3);
		main.setTop(endTurn.stack);
		
		endTurn.setOnMouseClicked(e -> {
			Label c =labels[game.getCurrentChampion().getLocation().x][game.getCurrentChampion().getLocation().y];
			if(((DropShadow)c.getGraphic().getEffect()).getColor().equals(Color.ORANGERED))
				((DropShadow)c.getGraphic().getEffect()).setColor(Color.PALEVIOLETRED);
			else 
				((DropShadow)c.getGraphic().getEffect()).setColor(Color.AQUA);
			
			System.out.println();
			game.endTurn();
			Label d =labels[game.getCurrentChampion().getLocation().x][game.getCurrentChampion().getLocation().y];
			if(((DropShadow)d.getGraphic().getEffect()).getColor().equals(Color.PALEVIOLETRED))
				((DropShadow)d.getGraphic().getEffect()).setColor(Color.ORANGERED);
			else 
				((DropShadow)d.getGraphic().getEffect()).setColor(Color.BLUE);
			System.out.println();
			System.out.println(PlayersNames.controller.game.getCurrentChampion().getName());
			for (Ability a : game.getCurrentChampion().getAbilities()) {
				System.out.println("Range" + a.getCastRange());
				System.out.println(a.getCastArea());
			}
			System.out.println("Team 1 Leader : " + game.getFirstPlayer().getLeader().getName() + ",  Team 2 Leader : "
					+ game.getSecondPlayer().getLeader().getName());
		});
		System.out.println(game.getCurrentChampion().getName());
		for (Ability a : game.getCurrentChampion().getAbilities()) {
			System.out.println("Range" + a.getCastRange());
			System.out.println(a.getCastArea());
		}
		System.out.println("Team 1 Leader : " + game.getFirstPlayer().getLeader().getName() + ", Team 2 Leader : "
				+ game.getSecondPlayer().getLeader().getName());
		boardScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				handleHelper(event);
			}
		});
		
	}

	public void handleHelper(KeyEvent event) {

		Champion c = game.getCurrentChampion();

		switch (event.getCode()) {
		case R: {
			if(check()) break;
			leaderAbility();
			break;
		}
		case SPACE: {
			if(check()) break;
			space = true;
			break;
		}
		case Q: {
			if(check()) break;
			q=ability(0);
			break;
		}
		case W: {
			if(check()) break;
			w=ability(1);
			break;
		}
		case E: {
			if(check()) break;
			e=ability(2);
			break;
		}
		case T: {
			if(check()) break;
			if (c.getAbilities().size() > 3) {
				singleTarget = true;
				singleTargetAbility(c.getAbilities().get(3));
			}
			break;
		}
		case UP, DOWN, LEFT, RIGHT: {
			directionUsed(event.getCode());
			break;
		}

		}
	}
	public boolean ability(int n) {
		Champion c =game.getCurrentChampion();
		boolean r = false;
		if (c.getAbilities().get(n).getCastArea() == AreaOfEffect.DIRECTIONAL) {
			r = true;
		}else if (c.getAbilities().get(n).getCastArea() == AreaOfEffect.SINGLETARGET) {
			singleTarget = true;
			singleTargetAbility(c.getAbilities().get(n));
		} else {
			normalAbilitiess(c.getAbilities().get(n));
		}
		return r;
	}
	public boolean check() {
		boolean r = false;
		if (q || w || e) {
			showFlashMessage("Choose The Ability Direction");
			r=true;
		} else if (singleTarget) {
			showFlashMessage("Choose a Target");
			r=true;
		} else if (space) {
			showFlashMessage("Choose The Attack Direction");
			r=true;
		}else if(!move) r=true;
		return r;
	}
	public void directionUsed(KeyCode d) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();

		Direction direction;

		if (d == KeyCode.UP) {
			direction = Direction.DOWN;
		} else if (d == KeyCode.DOWN) {
			direction = Direction.UP;
		} else if (d == KeyCode.LEFT) {
			direction = Direction.LEFT;
		} else {
			direction = Direction.RIGHT;
		}
		if (space) {
			attack(direction);
		} else if (q) {
			q = false;
			directionalAbility(c.getAbilities().get(0), direction);
		} else if (w) {
			w = false;
			directionalAbility(c.getAbilities().get(1), direction);
		} else if (e) {
			e = false;
			directionalAbility(c.getAbilities().get(2), direction);
		}else if (singleTarget) {
			showFlashMessage("Choose a Target");
		}else if (move) {
			move(direction);
		}

	}
	static boolean observe = true;
	public void move(Direction direction) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();
		if(observe) {
			animator.observe(gameGrid.getChildren());
			observe = false;
		}
		
		try {
			
			int x = c.getLocation().x;
			int y = c.getLocation().y;
			game.move(direction);
			move=false;
			GridPane.setConstraints(labels[x][y], c.getLocation().y, c.getLocation().x);
			labels[c.getLocation().x][c.getLocation().y] = labels[x][y];
			labels[x][y] = new Label();
		} catch (NotEnoughResourcesException | UnallowedMovementException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			showFlashMessage(e.getLocalizedMessage());
		}
	}

	public void attack(Direction d) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();

		try {
			game.attack(d);
			checkIfDead();

		} catch (NotEnoughResourcesException | UnallowedMovementException | ChampionDisarmedException
				| InvalidTargetException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			showFlashMessage(e.getLocalizedMessage());
		}
		space = false;

	}

	public void leaderAbility() {
		Champion c = PlayersNames.controller.game.getCurrentChampion();
		try {
			game.useLeaderAbility();
			checkIfDead();
		} catch (LeaderNotCurrentException | LeaderAbilityAlreadyUsedException | AbilityUseException
				| InvalidTargetException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			showFlashMessage(e.getLocalizedMessage());
		}
	}

	public void normalAbilitiess(Ability a) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();

		try {
			game.castAbility(a);
		} catch (AbilityUseException | NotEnoughResourcesException | InvalidTargetException
				| CloneNotSupportedException e) {
			System.out.println(e.getLocalizedMessage());
			showFlashMessage(e.getLocalizedMessage());
		}
	}

	public void directionalAbility(Ability a, Direction d) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();

		try {
			game.castAbility(a, d);
			checkIfDead();
		} catch (NotEnoughResourcesException | AbilityUseException | InvalidTargetException
				| CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getLocalizedMessage());
			showFlashMessage(e.getLocalizedMessage());
		}
	}

	public void singleTargetAbility(Ability a) {

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				Label label = labels[i][j];
				final int x = i;
				final int y = j;
				label.setOnMouseClicked(e -> {
					if (singleTarget == true) {
						try {
							System.out.println("i is " + x + " , j is " + y);
							game.castAbility(a, x, y);

						} catch (AbilityUseException | NotEnoughResourcesException | InvalidTargetException
								| CloneNotSupportedException e1) {
							System.out.println(e1.getLocalizedMessage());
							showFlashMessage(e1.getLocalizedMessage());
						}
						singleTarget = false;
					}
				});
			}

		}
	}

	static boolean xResized = false;
	static boolean yResized = false;

	private void showFlashMessage(String message) {

		Stage window = new Stage();
		window.initStyle(StageStyle.TRANSPARENT);

		VBox layout = new VBox();
		layout.setAlignment(Pos.CENTER);
		Label label = new Label(message);
		label.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));
		label.setTextFill(Color.WHITE);
		label.setTextAlignment(TextAlignment.CENTER);
		label.setWrapText(true);
		label.setMaxWidth(300);
		layout.getChildren().add(label);
		layout.setPadding(new Insets(3));
		layout.setStyle("-fx-background-color: transparent;");
		window.setScene(new Scene(layout, Color.TRANSPARENT));
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(Main.Stage);
		window.setAlwaysOnTop(true);

		final double x = (Main.Stage.getX() + stackImage.getScaleX() + gameGrid.getWidth() / 2 + 25);
		final double y = (Main.Stage.getY() + stackImage.getScaleY() + gameGrid.getHeight() / 2);

		window.widthProperty().addListener((observable, oldValue, newValue) -> {
			if (!xResized && newValue.intValue() > 1) {
				window.setX(x - newValue.intValue() / 2);
				xResized = true;
			}
		});

		window.heightProperty().addListener((observable, oldValue, newValue) -> {
			if (!yResized && newValue.intValue() > 1) {
				window.setY(y - newValue.intValue() / 2);
				yResized = true;
			}
		});

		xResized = false;
		yResized = false;
		Timeline timeline = new Timeline(
				new KeyFrame(Duration.ZERO, evt -> window.show(), new KeyValue(layout.opacityProperty(), 0)),
				new KeyFrame(Duration.millis(500), new KeyValue(layout.opacityProperty(), 1.0)),
				new KeyFrame(Duration.millis(1200), new KeyValue(layout.opacityProperty(), 0.2)));
		timeline.setOnFinished(evt -> window.close());

		timeline.play();

	}

	public void checkIfDead() {

		Object[][] board = PlayersNames.controller.game.getBoard();
		// AudioClip buzzer = new
		// AudioClip(getClass().getResource("/audio/buzzer.mp3").toExternalForm());

		int c = 0;
		for (int i = 0; i < Game.getBoardheight(); i++) {
			for (int j = 0; j < Game.getBoardwidth(); j++) {
				if (board[i][j] == null && labels[i][j].getId() != null) {
					if (c == 0) {
						String name;
						if (labels[i][j].getId() == "Cover") {
							name = "/resources/coverRemoved.mpeg";
						} else {
							name = "/resources/dead.mpeg";
						}
						AudioClip buzzer = new AudioClip(getClass().getResource(name).toExternalForm());
						buzzer.play();
					}
					gameGrid.getChildren().remove(labels[i][j]);
					labels[i][j] = new Label();
					c++;
				}
			}
		}
		Player winner = game.checkGameOver();
		if (winner != null) {
			Winner p = new Winner(winner);
		}
	}

	public void loadBoard() {
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
						//view.setImage(new Image(this.getClass().getResource("/resources/gif.gif").toExternalForm()));
						view.setPreserveRatio(true);
						label.setGraphic(view);
						if(game.getFirstPlayer().getTeam().contains((Champion) game.getBoard()[i][j])) {
							DropShadow ds = new DropShadow( 20, Color.AQUA );
				
							view.setEffect(ds);
						}else {
							DropShadow ds = new DropShadow( 20, Color.PALEVIOLETRED );
							view.setEffect(ds);
						}

					}
				}
				GridPane.setConstraints(label, j, i);
				
				labels[i][j] = label;
				gameGrid.getChildren().add(label);
				
			}
		}
		Label d =labels[game.getCurrentChampion().getLocation().x][game.getCurrentChampion().getLocation().y];
		if(((DropShadow)d.getGraphic().getEffect()).getColor().equals(Color.PALEVIOLETRED))
			((DropShadow)d.getGraphic().getEffect()).setColor(Color.ORANGERED);
		else 
			((DropShadow)d.getGraphic().getEffect()).setColor(Color.BLUE);
	}
//	public void errorLabel(String s) {
//
//		Label label = new Label(s);
//		label.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 35));
//
//		gameGrid.getChildren().add(label);
//
//		Timeline blinker = createBlinker(label);
//		blinker.setOnFinished(event -> label.setText(s));
//		FadeTransition fader = createFader(label);
//
//		SequentialTransition blinkThenFade = new SequentialTransition(label,
//				// blinker
//				fader
//
//		);
//		blinkThenFade.play();
//		blinkThenFade.setOnFinished(e -> {
//
//			gameGrid.getChildren().remove(label);
//		});
//
//	}
//
//	private Timeline createBlinker(Node node) {
//		Timeline blink = new Timeline(
//				new KeyFrame(Duration.seconds(0), new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)),
//				new KeyFrame(Duration.seconds(0.5), new KeyValue(node.opacityProperty(), 0, Interpolator.DISCRETE)),
//				new KeyFrame(Duration.seconds(1), new KeyValue(node.opacityProperty(), 1, Interpolator.DISCRETE)));
//		blink.setCycleCount(3);
//
//		return blink;
//	}
//
//	private FadeTransition createFader(Node node) {
//		FadeTransition fade = new FadeTransition(Duration.seconds(1), node);
//		fade.setFromValue(1);
//		fade.setToValue(0);
//
//		return fade;
//	}

}
