package hkust.edu.visualneo.utils.frontend;

import hkust.edu.visualneo.VisualNeoController;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import org.neo4j.driver.Value;

import java.util.HashMap;

abstract public class GraphElement extends Group {

    // Radius of the Vertex
    protected static final int VERTEX_RADIUS = 25;
    // A boolean variable indicates whether it can be selected and moved now
    public boolean canSelect = false;
    // Label shown on the GraphElement
    protected final Text label_displayed = new Text();
    // String of the label
    String label;
    // Properties attached to the element(node/relation)
    HashMap<String, Value> properties;

    // TODO: Remove these
//    Rectangle textBound = new Rectangle();
//    Circle textOrigin = new Circle();

    GraphElement() {
        // initialize the arraylist
        properties = new HashMap<>();
    }

    void mouseExited(MouseEvent m) {
        getScene().setCursor(Cursor.DEFAULT);
    }

    void mouseReleased(MouseEvent m) {
        if (VisualNeoController.getStatus() == VisualNeoController.Status.SELECT)
            mouseEntered(m);
    }

    public void addLabel(String new_label) {
        // Add the new label
        System.out.println("Add Label: " + new_label);
        label = new_label;
        label_displayed.setText(new_label);
        label_displayed.setTranslateX(-label_displayed.getLayoutBounds().getWidth() / 2);
        label_displayed.setTranslateY(label_displayed.getLayoutBounds().getHeight() / 2);

//        textBound.setWidth(label_displayed.getLayoutBounds().getWidth());
//        textBound.setHeight(label_displayed.getLayoutBounds().getHeight());
//        textBound.setTranslateX(-label_displayed.getLayoutBounds().getWidth() / 2);
//        textBound.setTranslateY(-label_displayed.getLayoutBounds().getHeight() / 2);
//        textBound.setLayoutX(label_displayed.getLayoutX());
//        textBound.setLayoutY(label_displayed.getLayoutY());
//        textOrigin.setLayoutX(label_displayed.getLayoutX());
//        textOrigin.setLayoutY(label_displayed.getLayoutY());
//        textBound.toFront();
//        textOrigin.toFront();
    }

    public String getLabel() {
        return label;
    }

    public HashMap<String, Value> getProp() {
        return properties;
    }

    protected void initializeShape() {
        getChildren().add(label_displayed);
        label_displayed.setBoundsType(TextBoundsType.VISUAL);

//        getChildren().addAll(textBound, textOrigin);
//        textBound.setFill(null);
//        textBound.setStroke(Color.BLUE);
//        textOrigin.setRadius(2.5);
//        textOrigin.setFill(Color.RED);
    }

    abstract public void becomeHighlight();

    abstract public void removeHighlight();

    abstract public void eraseFrom(VisualNeoController controller);

    abstract protected void pressed(MouseEvent m);

    abstract protected void mouseEntered(MouseEvent m);

    abstract public String toText();

}
