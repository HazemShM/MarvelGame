package views;

import java.util.ArrayList;
import java.util.Iterator;

import engine.Game;
import engine.Player;
import engine.PriorityQueue;
import exceptions.*;
import javafx.animation.*;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
	static boolean move = false;

	public void GameScene() {

		main = new BorderPane();
		ImageView imageview = new ImageView("/resources/marveliano.jpg");
		BoxBlur bb = new BoxBlur();
		imageview.setEffect(bb);
		bb.setIterations(3);
		main.setMaxSize(StartMenu.startScene.getWidth(), StartMenu.startScene.getHeight());
		imageview.fitWidthProperty().bind(StartMenu.startScene.widthProperty());
		imageview.fitHeightProperty().bind(StartMenu.startScene.heightProperty());
		main.getChildren().add(imageview);

		Main.swapScenes(main);
		Main.mediaPlayer.stop();
		main.setPadding(new Insets(5));
		gameGrid = new GridPane();
		gameGrid.setPrefHeight(500);
		gameGrid.setPrefWidth(500);
		labels = new Label[Game.getBoardwidth()][Game.getBoardheight()];
		// Scene boardScene = new Scene(main, 1200, 720, Color.BEIGE);

//		main.setMaxHeight(StartMenu.startScene.getHeight());
//		main.setMaxWidth(StartMenu.startScene.getWidth());
//		gameGrid.setMaxHeight(500);
//		gameGrid.setMaxWidth(500);
		// BorderPane.setMargin(gameGrid, new Insets(5));
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
		DropShadow drop = new DropShadow();
		backGround.setEffect(drop);
//		backGround.setFitHeight(500);
//		backGround.setFitWidth(500);
		stackImage = new StackPane();
		stackImage.setAlignment(Pos.TOP_CENTER);
		gameGrid.setAlignment(stackImage.getAlignment());
		stackImage.getChildren().addAll(backGround, gameGrid);
		stackImage.setPrefHeight(500);
		stackImage.setMaxHeight(500);
		stackImage.setPrefWidth(500);
		stackImage.setPrefWidth(500);

		main.setCenter(stackImage);

//		gameGrid.setBackground(new Background(new BackgroundImage(img, BackgroundRepeat.NO_REPEAT,
//				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));

		// Main.Stage.setScene(boardScene);
		animator = new LayoutAnimator();
		loadBoard();

		// GridPane bot = new GridPane();
		// grid(3, 1, bot);

		StyledButton endTurn = new StyledButton("EndTurn", 3);
		// bot.add(endTurn.stack, 1, 0);

		gameGrid.add(endTurn.stack, 1, 5);
		System.out.println(endTurn.stack.getId());
		// endTurn.stack.setAlignment(Pos.TOP_CENTER);

		turnOrderBox.setPrefHeight(150);
//		ImageView im = new ImageView("/resources/champion2.png");
//		ImageView im2 = new ImageView("/resources/Champions/Spiderman.png");
//		
//		im.setFitWidth(300);
//		im2.setFitWidth(200);
//		im2.setPreserveRatio(true);
//		im.setPreserveRatio(true);
//		StackPane temp = new StackPane();
//		temp.getChildren().addAll(im,im2);

		main.setTop(turnOrderBox);
		turnOrderBox.setAlignment(Pos.TOP_CENTER);
		showTurn();
		// main.getTop().prefHeight(200);

		hover();
//		main.getTop().prefHeight(200);

		updateBars(game.getCurrentChampion());

		left.getChildren().addAll(champPlayer,current, health, mana);
		current.setAlignment(Pos.CENTER);
		updateAbilities(game.getCurrentChampion());
		left.getChildren().add(abilitiesInfo);

		left.setPrefWidth(300);
		left.setMaxWidth(300);
		left.setAlignment(Pos.TOP_CENTER);
		right.setPrefWidth(300);
		right.setMaxWidth(300);
		right.setAlignment(Pos.TOP_LEFT);
//		right.setAlignment(Pos.TOP_CENTER);
		main.setLeft(left);
		teams();
		String font_name = Font.getFamilies().get(24);
		Font font = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 30);
		Label player1 = new Label(game.getFirstPlayer().getName());
		player1.setFont(font);
		player1.setTextFill(Color.AQUA);
		player1.setStyle("-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");
		Label player2 = new Label(game.getSecondPlayer().getName());
		player2.setFont(font);
		player2.setTextFill(Color.PALEVIOLETRED);
		player2.setStyle("-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");
		leaderAbility1.setText("Leader Ability Not Used");
		leaderAbility2.setText("Leader Ability Not Used");
		leaderAbility1.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 25));
		leaderAbility2.setFont(Font.font("Aguda", FontWeight.EXTRA_BOLD, 25));
		leaderAbility1.setTextFill(Color.GREEN);
		leaderAbility2.setTextFill(Color.GREEN);
		right.getChildren().addAll(player1, team1,leaderAbility1, player2, team2,leaderAbility2);
		main.setRight(right);
		
		champPlayer.setFont(font);
		champPlayer.setTextFill(Color.WHITE);
		champPlayer.setStyle("-fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );");
		endTurn.setOnAction(e -> {
			Label c = labels[game.getCurrentChampion().getLocation().x][game.getCurrentChampion().getLocation().y];
			if (((DropShadow) c.getGraphic().getEffect()).getColor().equals(Color.ORANGERED))
				((DropShadow) c.getGraphic().getEffect()).setColor(Color.PALEVIOLETRED);
			else
				((DropShadow) c.getGraphic().getEffect()).setColor(Color.AQUA);

			System.out.println();
			game.endTurn();
			showTurn();
			updateAbilities(game.getCurrentChampion());
			updateBars(game.getCurrentChampion());
			Label d = labels[game.getCurrentChampion().getLocation().x][game.getCurrentChampion().getLocation().y];
			if (((DropShadow) d.getGraphic().getEffect()).getColor().equals(Color.PALEVIOLETRED))
				((DropShadow) d.getGraphic().getEffect()).setColor(Color.ORANGERED);
			else
				((DropShadow) d.getGraphic().getEffect()).setColor(Color.BLUE);
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
		StartMenu.startScene.setOnKeyReleased(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				handleHelper(event);
			}
		});

	}

	public void abilitySound(Ability a) {
		if(a.getName().equals("Fully Charged"))
			return;
		String name = "/resources/sound/"+a.getName()+".mp3";

		AudioClip buzzer = new AudioClip(getClass().getResource(name).toExternalForm());
		Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, evt -> {
			buzzer.stop();
			buzzer.play();
		}), new KeyFrame(Duration.seconds(4), evt -> {
			buzzer.stop();
		}));
		timeline.play();
	}

	static Label champPlayer = new Label();
	static VBox left = new VBox();
	static VBox right = new VBox();
	static Popup currpopup = new Popup();
	static Popup popup1 = new Popup();
	static Popup popup2 = new Popup();
	static Popup popup3 = new Popup();
	static Popup popup4 = new Popup();
	static Popup popup5 = new Popup();
	static Popup popup6 = new Popup();
	
	public void championDetails(Champion champion, StackPane n, Player p,Popup popup) {
		popup.getContent().clear();
		
		VBox v = new VBox();
		Label c = new Label();
		c.setText(champion.getName());
		String font_name = Font.getFamilies().get(42);
		Font font = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 30);
		c.setFont(font);
		c.setTextFill(Color.YELLOW);

		String champ = "";

		if (champion instanceof Hero) {
			champ += "Type: Hero" + "\n";
		} else if (champion instanceof Villain) {
			champ += "Type: Villain" + "\n";
		} else {
			champ += "Type: AntiHero" + "\n";
		}
		if (p.getLeader().equals(champion))
			champ += "The Leader of " + p.getName() + "'s Team" + " \n";
		champ += "Curr HP: " + champion.getCurrentHP() + "\n";
		champ += "Max HP: " + champion.getMaxHP() + "\n";
		champ += "Mana: " + champion.getMana() + "\n";
		champ += "curr ActionPoints: " + champion.getCurrentActionPoints() + "\n";
		champ += "Max ActionPoints: " + champion.getMaxActionPointsPerTurn() + "\n";
		champ += "Speed: " + champion.getSpeed() + "\n";
		champ += "Attack Range: " + champion.getAttackRange() + "\n";
		champ += "Attack Damage: " + champion.getAttackDamage() + "\n";

		v.getChildren().add(c);
		v.setStyle("-fx-background-color: transparent;");

		String effects = "";
		for (model.effects.Effect effect : champion.getAppliedEffects()) {
			effects += "Currently Applied Effects Are:" + "\n";
			effects += effect.getName() + "\n";
			effects += "		Duration: " + effect.getDuration() + "\n";
			effects += "		Type: " + effect.getType();
		}
		Label info = new Label(champ+effects);

		Font font2 = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 25);
		info.setFont(font2);
		info.setTextFill(Color.WHITE);
		v.getChildren().add(info);
		popup.getContent().add(v);
		n.hoverProperty().addListener((obs, oldVal, newValue) -> {
			if (newValue) {
				Bounds bnds = n.localToScreen(n.getLayoutBounds());
				double x = bnds.getMinX() + n.getWidth();
				double y = bnds.getMinY() - v.getHeight() / 2 + n.getHeight() / 2;

				popup.show(n, x, y);


			} else {
				popup.hide();
			}
//	            double x = bnds.getMinX() + notedPane.getWidth();
//	            double y = bnds.getMinY() - stickyNotesPane.getHeight()/2 +notedPane.getHeight()/2;
		});
	}

	static VBox abilitiesInfo = new VBox();

	public void updateAbilities(Champion c) {
		ArrayList<Ability> abilities = c.getAbilities();
		if (!abilitiesInfo.getChildren().isEmpty())
			abilitiesInfo.getChildren().clear();

		for (int i = 0; i < 3; i++) {
			Label abilityLabel = new Label(abilities.get(i).getName());
			if (i == 0)
				abilityInfo("Q", abilities.get(i), abilityLabel);
			else if (i == 1)
				abilityInfo("W", abilities.get(i), abilityLabel);
			else if (i == 2)
				abilityInfo("E", abilities.get(i), abilityLabel);
			String font_name = Font.getFamilies().get(38);
			Font font = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 30);
			abilityLabel.setFont(font);
			abilityLabel.setTextFill(Color.WHITE);
			DropShadow in = new DropShadow();
			abilityLabel.setEffect(in);
			abilityLabel.setTextAlignment(TextAlignment.CENTER);
			abilityLabel.setAlignment(Pos.CENTER);
			abilityLabel.setWrapText(true);
			abilitiesInfo.setSpacing(20);
			abilitiesInfo.setPrefWidth(400);
			abilitiesInfo.setAlignment(Pos.CENTER);
			abilitiesInfo.getChildren().add(abilityLabel);
		}
		System.out.println(abilitiesInfo.getWidth() + "  " + abilitiesInfo.getHeight());
		if (c.getAbilities().size() > 3 && !(abilitiesInfo.getChildren().size() > 3)) {
			Label abilityLabel = new Label(abilities.get(4).getName());
			abilityInfo("T", abilities.get(4), abilityLabel);
			String font_name = Font.getFamilies().get(38);
			Font font = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 30);
			abilityLabel.setFont(font);
			abilityLabel.setTextFill(Color.WHITE);
			abilitiesInfo.getChildren().add(abilityLabel);
		}

	}

	public void abilityInfo(String s, Ability a, Label label) {
		String ability = "";
		Popup popup = new Popup();

		VBox v = new VBox();
		Label press = new Label();
		if (s.equals("Q"))
			press.setText("Press Q to Use ");
		else if (s.equals("W"))
			press.setText("Press W to Use ");
		else if (s.equals("E"))
			press.setText("Press E to Use ");
		else if (s.equals("T"))
			press.setText("Press T to Use ");
		String font_name = Font.getFamilies().get(42);
		Font font = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 30);
		press.setFont(font);
		press.setTextFill(Color.YELLOW);

		v.getChildren().add(press);
		v.setStyle("-fx-background-color: transparent;");
		ability += "	-Mana Cost: " + a.getManaCost() + "\n";
		ability += "	-BaseCoolDown: " + a.getBaseCooldown() + "\n";
		ability += "	-CurrentCoolDown: " + a.getCurrentCooldown() + "\n";
		ability += "	-Range: " + a.getCastRange() + "\n";
		ability += "	-AOE: " + a.getCastArea() + "\n";
		ability += "	-AtionPoints: " + a.getRequiredActionPoints() + "\n";

		if (a instanceof CrowdControlAbility) {
			ability += "	-Type: CrowdControl" + "\n";
			ability += "	-Effect: " + "\n";
			ability += "		Name: " + ((CrowdControlAbility) a).getEffect().getName() + "\n";
			ability += "		Duration: " + ((CrowdControlAbility) a).getEffect().getDuration() + "\n";
			ability += "		Type: " + ((CrowdControlAbility) a).getEffect().getType() + "\n";

		} else if (a instanceof DamagingAbility) {
			ability += "	-Type: Damaging" + "\n";
			ability += "	-Damage Amount: " + ((DamagingAbility) a).getDamageAmount() + "\n";

		} else if (a instanceof HealingAbility) {
			ability += "	-Type: Healing" + "\n";
			ability += "	-Heal Amount: " + ((HealingAbility) a).getHealAmount() + "\n";
		}
		Label info = new Label(ability);
		Font font2 = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, 25);
		info.setFont(font2);
		info.setTextFill(Color.WHITE);
		v.getChildren().add(info);
		popup.getContent().add(v);
		label.hoverProperty().addListener((obs, oldVal, newValue) -> {
			if (newValue) {
				Bounds bnds = label.localToScreen(label.getLayoutBounds());
				double x = bnds.getMinX() + label.getWidth();
				double y = bnds.getMinY() - v.getHeight() / 2 + label.getHeight() / 2;
				popup.show(label, x, y);
			} else {
				popup.hide();
			}
//	            double x = bnds.getMinX() + notedPane.getWidth();
//	            double y = bnds.getMinY() - stickyNotesPane.getHeight()/2 +notedPane.getHeight()/2;
		});
	}

//	public String championInfo(Champion c) {
//		String champ = "";
//		if(c instanceof Hero) {
//			champ += "Type: Hero" + "\n";
//		}else if (c instanceof Villain) {
//			champ += "Type: Villain" + "\n";
//		}else {
//			champ += "Type: AntiHero" + "\n";
//		}
//		champ += "Max HP: " + c.getMaxHP() + "\n";
//		champ += "Mana: " + c.getMana()  + "\n";
//		champ += "Max ActionPoints: " + c.getMaxActionPointsPerTurn() + "\n";
//		champ += "Speed: " + c.getSpeed()  + "\n";
//		champ += "Attack Range: " +c.getAttackRange()  + "\n";
//		champ += "Attack Damage: " +c.getAttackDamage() + "\n";
//		champ +=   "\n";
//	}
	static HBox team1 = new HBox();
	static HBox team2 = new HBox();
	
	public void teams() {
		ArrayList<Champion> team;
		HBox curr;
		Color color;
		updateAbilities(game.getCurrentChampion());
		championDetails(game.getCurrentChampion(),current,((game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())? game.getFirstPlayer() :game.getSecondPlayer())), currpopup);
		for (int j = 0; j < 2; j++) {
			boolean flag ;
			if (j == 0) {
				team = game.getFirstPlayer().getTeam();
				curr = team1;
				color = Color.AQUA;
				flag =true;
			} else {
				team = game.getSecondPlayer().getTeam();
				curr = team2;
				color = Color.PALEVIOLETRED;
				flag =false;
			}
			if (curr.getChildren().isEmpty()) {
				int count=0;
				for (Champion c : team) {
					
					StackPane main = new StackPane();
					String name = "/resources/Champions/" + c.getName() + ".png";
					Image img = new Image(name);
					ImageView image = new ImageView(img);
					Circle t = new Circle();
					t.setFill(color);
					t.setRadius(50);
					image.setFitWidth(80);
					image.setFitHeight(80);
					image.setPreserveRatio(true);
					DropShadow in = new DropShadow();
					t.setEffect(in);
					main.setId(c.getName());
					main.getChildren().addAll(t, image);
					main.setPadding(new Insets(8));
					curr.getChildren().add(main);
					curr.setPrefWidth(350);
					curr.setAlignment(Pos.CENTER);
					team1.setPrefWidth(300);
					team2.setPrefWidth(300);
					team1.setMaxWidth(300);
					team2.setMaxWidth(300);
					team1.setAlignment(Pos.BASELINE_LEFT);
					team2.setAlignment(Pos.BASELINE_LEFT);
					
					
					if (flag) {
						if(count==0)
							championDetails(c, main, game.getFirstPlayer(), popup1);
						else if (count ==1) {
							championDetails(c, main, game.getFirstPlayer(), popup2);
						}else if(count==2) {
							championDetails(c, main, game.getFirstPlayer(), popup3);
						}
					}else {
						if(count==0)
							championDetails(c, main, game.getSecondPlayer(), popup4);
						else if (count ==1) {
							championDetails(c, main, game.getSecondPlayer(), popup5);
						}else if(count==2) {
							championDetails(c, main, game.getSecondPlayer(), popup6);
						}
					}
					count++;
					
				}
			} else {
				int count=0;
				for (int i = 0; i < curr.getChildren().size(); i++) {
					Node n = curr.getChildren().get(i);
					boolean found = false;
					Champion c=null;
					for (Champion t : team) {
						if (t.getName().equals(n.getId())) {
							found = true;
							c=t;
						}
					}
					if(c!=null && found)
					if (flag) {
						if(count==0)
							championDetails(c,(StackPane) n, game.getFirstPlayer(), popup1);
						else if (count ==1) {
							championDetails(c, (StackPane) n, game.getFirstPlayer(), popup2);
						}else if(count==2) {
							championDetails(c, (StackPane) n, game.getFirstPlayer(), popup3);
						}
					}else {
						if(count==0)
							championDetails(c, (StackPane) n, game.getSecondPlayer(), popup4);
						else if (count ==1) {
							championDetails(c, (StackPane) n, game.getSecondPlayer(), popup5);
						}else if(count==2) {
							championDetails(c, (StackPane) n, game.getSecondPlayer(), popup6);
						}
					}
					count++;
					if (!found) {
						curr.getChildren().remove(n);
						i--;
					}
					
				}
			}
		}
	}
	
	static ProgressBar health = new ProgressBar();
	static ProgressBar mana = new ProgressBar();

	public void updateBars(Champion current) {
		healthBar(current);
		manaBar(current);
	}

	public void healthBar(Champion current) {

		float l = current.getCurrentHP();
		float r = current.getMaxHP();
		float progress = l / r;
		health.setProgress(progress);

		Tooltip tool = new Tooltip();
		if (health.getTooltip() == null) {
			health.setPrefSize(300, 25);
			Tooltip.install(health, tool);
			tool.autoFixProperty();
			tool.autoHideProperty();
			health.setStyle("-fx-accent: #00FF00;" + "-fx-background-color: transparent ;");

		}
		tool.setText(l + "/" + r + " HP");
		// tool.setFont(null);

	}

	public void manaBar(Champion current) {

		float l = current.getMana();
		float r = current.maxMana;
		float progress = l / r;
		mana.setProgress(progress);

		Tooltip tool = new Tooltip();
		if (mana.getTooltip() == null) {
			mana.setPrefSize(300, 25);
			Tooltip.install(mana, tool);
			tool.autoFixProperty();
			tool.autoHideProperty();
			mana.setStyle("-fx-accent: Blue;");
		}
		tool.setText(l + "/" + r + " Mana");

	}

	// ArrayList<StackPane> circles = new ArrayList<StackPane>();
	static HBox turnOrderBox = new HBox();
	static StackPane current = new StackPane();

	public void showTurn() {

		if (!turnOrderBox.getChildren().isEmpty()) {
			StackPane temp = (StackPane) turnOrderBox.getChildren().remove(0);
//			if(turnOrderBox.getChildren().isEmpty()) {
//				showTurn();
//				return;
//			}		
			Circle f1 = (Circle) temp.getChildren().get(0);
			DropShadow in = new DropShadow();
			f1.setEffect(in);
			ImageView f2 = (ImageView) temp.getChildren().get(1);
			f1.setRadius(80);

			f1.setFill(Color.PALEVIOLETRED);
			f2.setFitWidth(130);
			f2.setFitHeight(130);
			f2.setPreserveRatio(true);
			current.getChildren().clear();
			current.getChildren().add(f1);
			current.getChildren().add(f2);
			current.setPadding(new Insets(8));
			Player p;
			if(game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())) {
				p=game.getFirstPlayer();
				champPlayer.setText(game.getFirstPlayer().getName());
			}else {
				champPlayer.setText(game.getSecondPlayer().getName());
				p=game.getSecondPlayer();
			}
			
			championDetails(game.getCurrentChampion(), current,p,currpopup);
			return;
		}
		ArrayList<Champion> Champions = new ArrayList<Champion>();
		PriorityQueue turnOrder = game.getTurnOrder();
		PriorityQueue temp = new PriorityQueue(6);

		int size = turnOrder.size();

		while (!turnOrder.isEmpty()) {
			Comparable current = turnOrder.remove();
			temp.insert(current);
			Champions.add((Champion) current);
		}
		while (!temp.isEmpty()) {
			turnOrder.insert(temp.remove());
		}
		for (Champion r : Champions) {
			StackPane main = new StackPane();
			String name = "/resources/Champions/" + r.getName() + ".png";
			Image img = new Image(name);
			ImageView image = new ImageView(img);
			Circle t = new Circle();
			t.setFill(Color.GREEN);
			if (r.equals(turnOrder.peekMin())) {
				t.setRadius(80);
				t.setFill(Color.PALEVIOLETRED);
				image.setFitWidth(130);
				image.setFitHeight(130);
				image.setPreserveRatio(true);
				DropShadow in = new DropShadow();
				t.setEffect(in);
				current.getChildren().clear();
				current.getChildren().add(t);
				current.getChildren().add(image);
				current.setPadding(new Insets(8));
				Player p;
				if(game.getFirstPlayer().getTeam().contains(game.getCurrentChampion())) {
					p=game.getFirstPlayer();
					champPlayer.setText(game.getFirstPlayer().getName());
				}else {
					champPlayer.setText(game.getSecondPlayer().getName());
					p=game.getSecondPlayer();
				}
				
				championDetails(game.getCurrentChampion(), current,p,currpopup);
			} else {
				t.setRadius(40);

				image.setFitWidth(65);
				image.setFitHeight(65);
				image.setPreserveRatio(true);
				main.getChildren().add(t);
				main.getChildren().add(image);
				turnOrderBox.getChildren().add(main);
			}
		}

	}

	public static void grid(int numCols, int numRows, GridPane g) {

		for (int i = 0; i < numCols; i++) {
			ColumnConstraints colConst = new ColumnConstraints();

			g.getColumnConstraints().add(colConst);
		}
		for (int i = 0; i < numRows; i++) {
			RowConstraints rowConst = new RowConstraints();

			g.getRowConstraints().add(rowConst);
		}
	}

	public void hover() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				int maxHealth;
				int currHealth;
				Tooltip tool = new Tooltip();
				Damageable d = (Damageable) game.getBoard()[i][j];
//				if(labels[i][j].getTooltip()!=null) {
//					currHealth = d.getCurrentHP();
//					if(d instanceof Champion) {
//						maxHealth=((Champion)d).getMaxHP();
//						labels[i][j].getTooltip().setText(currHealth+"/"+maxHealth +" HP");
//						
//					}else {
//						labels[i][j].getTooltip().setText(currHealth+" HP");
//					}
//				}
				if (d != null) {
					currHealth = d.getCurrentHP();
					if (d instanceof Champion) {
						maxHealth = ((Champion) d).getMaxHP();
						tool.setText(currHealth + "/" + maxHealth + " HP");
						tool.autoFixProperty();
						tool.autoHideProperty();
						Tooltip.install(labels[i][j], tool);

					} else {
						tool.setText(currHealth + " HP");
						Tooltip.install(labels[i][j], tool);
					}
				}
			}
		}
	}

	public void handleHelper(KeyEvent event) {

		Champion c = game.getCurrentChampion();

		switch (event.getCode()) {
		case R: {
			if (check())
				break;
			leaderAbility();

			break;
		}
		case SPACE: {
			if (check())
				break;
			space = true;
			break;
		}
		case Q: {
			if (check())
				break;
			q = ability(0);

			break;
		}
		case W: {
			if (check())
				break;
			w = ability(1);

			break;
		}
		case E: {
			if (check())
				break;
			e = ability(2);

			break;
		}
		case T: {
			if (check())
				break;
			if (c.getAbilities().size() > 3) {
				singleTarget = true;
				singleTargetAbility(c.getAbilities().get(3), 0, 0);

			}
			break;
		}
		case UP, DOWN, LEFT, RIGHT: {
			directionUsed(event.getCode());
			updateBars(game.getCurrentChampion());
			teams();
			hover();
			break;
		}

		}
	}

	public boolean ability(int n) {
		Champion c = game.getCurrentChampion();
		boolean r = false;
		if (c.getAbilities().get(n).getCastArea() == AreaOfEffect.DIRECTIONAL) {
			r = true;
		} else if (c.getAbilities().get(n).getCastArea() == AreaOfEffect.SINGLETARGET) {
			singleTarget = true;
			singleTargetAbility(c.getAbilities().get(n), 0, 0);
		} else {
			normalAbilitiess(c.getAbilities().get(n));
		}
		updateBars(game.getCurrentChampion());
		teams();
		hover();
		return r;
	}

	public boolean check() {
		boolean r = false;
		if (q || w || e) {
			errorMessage("Choose The Ability Direction", 2);
			r = true;
		} else if (singleTarget) {
			errorMessage("Choose a Target", 2);
			r = true;
		} else if (space) {
			errorMessage("Choose The Attack Direction", 2);
			r = true;
		} else if (move)
			r = true;
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
		} else if (singleTarget) {
			errorMessage("Choose a Target", 2);
		} else if (!move) {
			move(direction);
		}

	}

	static boolean observe = true;
	static boolean cast = false;

	public void move(Direction direction) {
		move = true;
		Champion c = PlayersNames.controller.game.getCurrentChampion();
		if (observe) {
			ObservableList<Node> nodes = gameGrid.getChildren();
			for (Node node : nodes) {
				if (node != null && node.getId() != null && node.getId() != "Cover") {
					animator.observe(node);
				}
			}

			observe = false;
		}

		try {

			int x = c.getLocation().x;
			int y = c.getLocation().y;
			game.move(direction);
			Label temp = labels[c.getLocation().x][c.getLocation().y];
			labels[c.getLocation().x][c.getLocation().y] = labels[x][y];
			labels[x][y] = temp;
//			gameGrid.getChildren().remove(labels[c.getLocation().x][c.getLocation().y]);
			GridPane.setConstraints(labels[c.getLocation().x][c.getLocation().y], c.getLocation().y, c.getLocation().x);
			GridPane.setConstraints(labels[x][y], y, x);
			
//			labels[c.getLocation().x][c.getLocation().y].setOnMouseClicked(e -> {
//				if (singleTarget == true) {
//					cast = true;
//					updateBars(game.getCurrentChampion());
//					teams();
//					hover();
//				}
//			});
//			labels[x][y].setOnMouseClicked(e -> {
//				if (singleTarget == true) {
//					cast = true;
//					updateBars(game.getCurrentChampion());
//					teams();
//					hover();
//				}
//			});

		} catch (NotEnoughResourcesException | UnallowedMovementException e) {
			// TODO Auto-generated catch block
			move = false;
			errorMessage(e.getLocalizedMessage(), 1);
		}
	}
	
	Timeline attack = new Timeline();
	Champion currChamp=game.getCurrentChampion();
	Label champLabel;
	Image old ;
	int countTemp=1;
	public void attackAnimation(Direction d) {
		if(countTemp==1) {
			champLabel=labels[currChamp.getLocation().x][currChamp.getLocation().y];
			old =(Image) ((ImageView)champLabel.getGraphic()).getImage();
			countTemp++;
		}	
		attack.stop();
		((ImageView)champLabel.getGraphic()).setImage(old);
		currChamp = game.getCurrentChampion();
		champLabel=labels[currChamp.getLocation().x][currChamp.getLocation().y];
		old =(Image) ((ImageView)champLabel.getGraphic()).getImage();
		
		if(currChamp.getName().equals("Iceman") || currChamp.getName().equals("Yellow Jacket")) return;
		
		String name="/resources/animation/";
		if (currChamp.getName().equals("Electro") || currChamp.getName().equals("Dr Strange")) {
			 if(d==Direction.RIGHT || d== Direction.UP)
					name+=currChamp.getName()+"AttackR.png";
			else if(d==Direction.LEFT || d== Direction.DOWN)
					name+=currChamp.getName()+"AttackL.png";
		}else if(d==Direction.UP && currChamp.getName().equals("Captain America"))
			name+=currChamp.getName()+"AttackD.gif";
		else if(d==Direction.DOWN && currChamp.getName().equals("Captain America"))
			name+=currChamp.getName()+"AttackUp.gif";
		else if(d==Direction.RIGHT || d== Direction.UP)
			name+=currChamp.getName()+"AttackR.gif";
		else if(d==Direction.LEFT || d== Direction.DOWN)
			name+=currChamp.getName()+"AttackL.gif";
		
		final String temp =name;
		attack = new Timeline(
				new KeyFrame(Duration.ZERO, evt -> ((ImageView) champLabel.getGraphic()).setImage(new Image(this.getClass().getResource(temp).toExternalForm()))),
				new KeyFrame(Duration.millis(500)),
				new KeyFrame(Duration.millis(700)));
		attack.play();
		attack.setOnFinished(evt -> {
			((ImageView)champLabel.getGraphic()).setImage(old);
		});
		
		
		
		
	}
	public void attack(Direction d) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();
		
		try {
			game.attack(d);
			attackAnimation(d);
			checkIfDead();

		} catch (NotEnoughResourcesException | UnallowedMovementException | ChampionDisarmedException
				| InvalidTargetException e) {
			// TODO Auto-generated catch block

			errorMessage(e.getLocalizedMessage(), 1);
		}
		space = false;

	}
	Label leaderAbility1=new Label();
	Label leaderAbility2=new Label();
	public void leaderAbility() {
		Champion c = PlayersNames.controller.game.getCurrentChampion();
		try {
			game.useLeaderAbility();
			if(game.getFirstPlayer().getLeader().equals(game.getCurrentChampion())) {
				leaderAbility1.setText("Leader Ability Used");
				leaderAbility1.setTextFill(Color.RED);
			}else {
				leaderAbility2.setText("Leader Ability Used");
				leaderAbility2.setTextFill(Color.RED);
			}
			updateBars(game.getCurrentChampion());
			teams();
			hover();
			checkIfDead();
		} catch (LeaderNotCurrentException | LeaderAbilityAlreadyUsedException | AbilityUseException
				| InvalidTargetException e) {
			// TODO Auto-generated catch block
			errorMessage(e.getLocalizedMessage(), 1);
		}
	}

	public void normalAbilitiess(Ability a) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();

		try {
			game.castAbility(a);
			abilitySound(a);
		} catch (AbilityUseException | NotEnoughResourcesException | InvalidTargetException
				| CloneNotSupportedException e) {

			errorMessage(e.getLocalizedMessage(), 1);
		}
	}

	public void directionalAbility(Ability a, Direction d) {
		Champion c = PlayersNames.controller.game.getCurrentChampion();

		try {
			game.castAbility(a, d);
			abilitySound(a);
			checkIfDead();
		} catch (NotEnoughResourcesException | AbilityUseException | InvalidTargetException
				| CloneNotSupportedException e) {
			// TODO Auto-generated catch block

			errorMessage(e.getLocalizedMessage(), 1);
		}
	}

	public void singleTargetAbility(Ability a, int f, int u) {
//		if(singleTarget && cast) {
//			try {
//				game.castAbility(a, f, u);
//				updateBars(game.getCurrentChampion());
//				teams();
//				hover();
//			} catch (AbilityUseException | NotEnoughResourcesException | InvalidTargetException
//					| CloneNotSupportedException e1) {
//
//				errorMessage(e1.getLocalizedMessage(), 1);
//
//			}
//			cast=false;
//			singleTarget = false;
//			return;
//		}
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				Label label = labels[i][j];
				final int x = i;
				final int y = j;
				label.setOnMouseClicked(e -> {
					if (singleTarget == true) {
						try {
							game.castAbility(a, x, y);
							abilitySound(a);
							updateBars(game.getCurrentChampion());
							teams();
							hover();
						} catch (AbilityUseException | NotEnoughResourcesException | InvalidTargetException
								| CloneNotSupportedException e1) {

							errorMessage(e1.getLocalizedMessage(), 1);

						}
						singleTarget = false;
					}
				});
			}

		}
	}

	static boolean xResized = false;
	static boolean yResized = false;

	private void errorMessage(String message, int color) {

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
//		layout.setPadding(new Insets(3));
		layout.setStyle("-fx-background-color: transparent;");
		window.setScene(new Scene(layout, Color.TRANSPARENT));
		window.initModality(Modality.APPLICATION_MODAL);
		window.initOwner(Main.Stage);
		window.setAlwaysOnTop(true);

		final double x = (Main.Stage.getX() + gameGrid.getLayoutX() + gameGrid.getWidth() / 2 +310);
		final double y = (Main.Stage.getY() + gameGrid.getLayoutY() + gameGrid.getHeight() / 2);

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
						String name;
						if (label.getId().equals("Captain America") || label.getId().equals("Venom")
								|| label.getId().equals("Ghost Rider"))
							name = "/resources/animation/" + ((Champion) game.getBoard()[i][j]).getName() + ".gif";
						else
							name = "/resources/animation/" + ((Champion) game.getBoard()[i][j]).getName() + ".png";
						Image img = new Image(this.getClass().getResource(name).toExternalForm());
						ImageView view = new ImageView(img);
						view.setPreserveRatio(true);
						label.setGraphic(view);
						if (game.getFirstPlayer().getTeam().contains((Champion) game.getBoard()[i][j])) {
							DropShadow ds = new DropShadow(20, Color.AQUA);

							view.setEffect(ds);
						} else {
							DropShadow ds = new DropShadow(20, Color.PALEVIOLETRED);
							view.setEffect(ds);
						}

					}
				}
				GridPane.setConstraints(label, j, i);

				labels[i][j] = label;
				gameGrid.getChildren().add(label);

			}
		}
		Label d = labels[game.getCurrentChampion().getLocation().x][game.getCurrentChampion().getLocation().y];
		if (((DropShadow) d.getGraphic().getEffect()).getColor().equals(Color.PALEVIOLETRED))
			((DropShadow) d.getGraphic().getEffect()).setColor(Color.ORANGERED);
		else
			((DropShadow) d.getGraphic().getEffect()).setColor(Color.BLUE);
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
