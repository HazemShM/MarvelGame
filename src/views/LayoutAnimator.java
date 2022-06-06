package views;

import javafx.animation.TranslateTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Animates an object when its position is changed. For instance, when
 * additional items are added to a Region, and the layout has changed, then the
 * layout animator makes the transition by sliding each item into its final
 * place.
 */
public class LayoutAnimator implements ChangeListener<Number>, ListChangeListener<Node> {

	private Map<Node, TranslateTransition> nodeXTransitions = new HashMap<>();
	private Map<Node, TranslateTransition> nodeYTransitions = new HashMap<>();

	/**
	 * Animates all the children of a Region. <code>
	 *   VBox myVbox = new VBox();
	 *   LayoutAnimator animator = new LayoutAnimator();
	 *   animator.observe(myVbox.getChildren());
	 * </code>
	 *
	 * @param nodes
	 */
	public void observe(ObservableList<Node> nodes) {
		for (Node node : nodes) {
			this.observe(node);
		}
		nodes.addListener(this);
	}

	public void unobserve(ObservableList<Node> nodes) {
		nodes.removeListener(this);
	}

	public void observe(Node n) {
		n.layoutXProperty().addListener(this);
		n.layoutYProperty().addListener(this);
	}

	public void unobserve(Node n) {
		n.layoutXProperty().removeListener(this);
		n.layoutYProperty().removeListener(this);
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
		final double delta = newValue.doubleValue() - oldValue.doubleValue();
		final DoubleProperty doubleProperty = (DoubleProperty) ov;
		final Node node = (Node) doubleProperty.getBean();
		boolean d=false;
		TranslateTransition t;
		switch (doubleProperty.getName()) {
		case "layoutX":
			t = nodeXTransitions.get(node);
			if (t == null) {
				t = new TranslateTransition(Duration.millis(1200), node);
				t.setToX(0);
				nodeXTransitions.put(node, t);
				
			}
			d=true;
			t.setFromX(node.getTranslateX() - delta);
			node.setTranslateX(node.getTranslateX() - delta);
			break;

		default: // "layoutY"
			t = nodeYTransitions.get(node);
			if (t == null) {
				t = new TranslateTransition(Duration.millis(1200), node);
				t.setToY(0);
				nodeYTransitions.put(node, t);
				
			}
			d=false;
			t.setFromY(node.getTranslateY() - delta);
			node.setTranslateY(node.getTranslateY() - delta);
		}
		changeImage(((Label) node),t,delta,d);
		
		t.playFromStart();
	}

	public void changeImage(Label label,TranslateTransition t , double delta,boolean d) {
		Image view = ((ImageView)label.getGraphic()).getImage();
//		
//		String left = "/resources/animation/"+label.getId()+"MoveL.gif";
//		String right = "/resources/animation/"+label.getId()+"MoveR.gif";
//		String up = "/resources/animation/"+label.getId()+"MoveL.gif";
//		String down = "/resources/animation/"+label.getId()+"MoveR.gif";
//		
		String left = "/resources/animation/Captain AmericaMoveL.gif";
		String right = "/resources/animation/Captain AmericaMoveR.gif";
		String up = "/resources/animation/SpidermanMoveL.gif";
		String down = "/resources/animation/SpidermanMoveR.gif";
		String current;
		if(delta>0) {
			if(d) current=right; else current = down;
			
			((ImageView)label.getGraphic()).setImage(new Image(this.getClass().getResource(current).toExternalForm()));
		
		}else {
			if(d) current=left; else current =up ;

			((ImageView)label.getGraphic()).setImage(new Image(this.getClass().getResource(current).toExternalForm()));
		}
		t.setOnFinished(e->{
			((ImageView)label.getGraphic()).setImage(view);
			GameBoard.move=true;
		});
	
	
	}
	@Override
	public void onChanged(Change change) {
		while (change.next()) {
			if (change.wasAdded()) {
				for (Node node : (List<Node>) change.getAddedSubList()) {
					this.observe(node);
				}
			} else if (change.wasRemoved()) {
				for (Node node : (List<Node>) change.getRemoved()) {
					this.unobserve(node);
				}
			}
		}
	}
}