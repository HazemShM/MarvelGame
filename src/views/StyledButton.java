package views;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.event.EventHandler;

public class StyledButton {
	StackPane stack;
	ImageView image;
	Text text;
	Blend blend;
	public StyledButton(String s, int style) {
		Image pImg = new Image("/resources/buttons/button" + style + ".png");
		image = new ImageView(pImg);

		image.setFitHeight(100);
		image.setFitWidth(300);
		image.setPreserveRatio(true);
		DropShadow dropShadow = new DropShadow();
		InnerShadow innerShadow = new InnerShadow();

		Distant light = new Distant();
		light.setAzimuth(-135.0f);
		Lighting l = new Lighting();
		l.setLight(light);
		l.setSurfaceScale(5.0f);
		// Creating the blend
		blend = new Blend();
		blend.setMode(BlendMode.ADD);
		// Setting both the shadow effects to the blend
		blend.setBottomInput(l);
		
		blend.setBottomInput(dropShadow);
		
		image.setOnMouseEntered(e -> {
			image.setEffect(innerShadow);
			text.setEffect(innerShadow);
		});
		image.setOnMouseExited(e -> {
			image.setEffect(blend);
			text.setEffect(blend);
		});

		image.setOnMouseClicked(e -> {
			String name = getClass().getResource("/resources/clickSound.mp3").toString();
			AudioClip buzzer = new AudioClip(name);
			buzzer.play();
			buzzer.setRate(3);
		});
		text  =new Text(s);
		text.setEffect(blend);
		image.setEffect(blend);
		// 15,16,24,(38),42,46
		String FONT_PATH = "/resources/fonts/Fighting_Spirit_2_bold.ttf";
		text.setFont(Font.loadFont(getClass().getResourceAsStream(FONT_PATH), 35));
		//String font_name = Font.getFamilies().get(24);
//	    System.out.println("Font Name:"+font_name);
		int size = 30;
		//Font font = Font.font(font_name, FontWeight.BOLD, FontPosture.REGULAR, size);
		//text.setFont(font);
		text.setOnMouseEntered(e -> {
			image.setEffect(innerShadow);
			text.setEffect(innerShadow);
		});
		text.setOnMouseExited(e -> {
			image.setEffect(blend);
			text.setEffect(blend);
		});

		text.setTextAlignment(TextAlignment.CENTER);
		//text.setTranslateY(1);
		text.setOnMouseClicked(e -> {
			String name = getClass().getResource("/resources/clickSound.mp3").toString();
			AudioClip buzzer = new AudioClip(name);
			buzzer.play();
			buzzer.setRate(3);
		});
		if (style == 2)
			text.setFill(Color.STEELBLUE);
		else {
			text.setFill(Color.WHITE);
		}
		stack = new StackPane();
		stack.setPrefHeight(100);
		stack.setPrefWidth(300);
		stack.getChildren().add(image);
		stack.getChildren().add(text);

	}

	public void setDisable(Boolean t) {
		image.setDisable(t);
		text.setDisable(t);
		
		if (t) {
			image.setEffect(new InnerShadow());
			text.setEffect(new InnerShadow());
		} else {
			image.setEffect(blend);
			text.setEffect(blend);
		}

	}

	public <E extends Event> void setOnMouseClicked(EventHandler<E> handler) {
		@SuppressWarnings("unchecked")
		EventType<E> eventType = (EventType<E>) MouseEvent.MOUSE_CLICKED;

		image.addEventHandler(eventType, handler);
		text.addEventHandler(eventType, handler);
	}

	public <E extends Event> void addEvent(EventType<E> eventType, EventHandler<E> handler) {
		image.addEventHandler(eventType, handler);
		text.addEventHandler(eventType, handler);

	}

	public <E extends Event> void removeEvent(EventType<E> eventType, EventHandler<E> handler) {
		image.removeEventHandler(eventType, handler);
		text.removeEventHandler(eventType, handler);

	}

}
