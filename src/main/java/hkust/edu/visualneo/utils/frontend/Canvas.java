package hkust.edu.visualneo.utils.frontend;

import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.EventTarget;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class Canvas extends Pane {

    private static final double UNIT_SCROLL = 32.0;

    public final OrthogonalCamera camera = new OrthogonalCamera(this);

    private final ObservableSet<GraphElement> highlights =
            new SimpleSetProperty<>(this, "highlights", FXCollections.observableSet());

    private final ObservableSet<GraphElement> unmodifiableHighlights =
            FXCollections.unmodifiableObservableSet(highlights);

    private Point2D cursor;
    private boolean dragged;

    public Canvas() {
        super();

        getHighlights().addListener((SetChangeListener<GraphElement>) c -> {
            if (c.wasAdded()) {
                c.getElementAdded().setHighlight(true);
                if (c.getElementAdded() instanceof Vertex vertex)
                    vertex.toFront();
            }
            else
                c.getElementRemoved().setHighlight(false);
        });

        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.isShortcutDown()) {
                if (e.getCode() == KeyCode.A)
                    getChildren().forEach(node -> addHighlight((GraphElement) node));
            }
            else {
                if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE)
                    removeElements(getHighlights());
            }
        });

        setOnMousePressed(e -> {
            EventTarget target = e.getTarget();

            if (target == this) {  // Clicked on Canvas
                if (e.isShiftDown())
                    createVertex(e.getX(), e.getY());
                else {
                    clearHighlights();
                    cursor = new Point2D(e.getX(), e.getY());
                }
            }
            else {  // Clicked on a GraphElement
                GraphElement currentElement = (GraphElement) ((Node) target).getParent();
                Vertex lastVertex = nomineeVertex();
                if (e.isShiftDown() && lastVertex != null && currentElement instanceof Vertex currentVertex)
                    createEdge(lastVertex, currentVertex);
                else if (e.isShortcutDown()) {
                    if (currentElement.isHighlighted())
                        removeHighlight(currentElement);
                    else
                        addHighlight(currentElement);
                }
                else {
                    if (!currentElement.isHighlighted()) {
                        clearHighlights();
                        addHighlight(currentElement);
                    }
                    cursor = new Point2D(e.getX(), e.getY());
                }
            }
        });

        setOnMouseDragged(e -> {
            if (cursor == null)
                return;

            EventTarget target = e.getTarget();

            if (target == this) {
                camera.translate(-(e.getX() - cursor.getX()), -(e.getY() - cursor.getY()));
            }
            else {
                GraphElement currentElement = (GraphElement) ((Node) target).getParent();
                if (currentElement.isHighlighted()) {
                    Point2D delta = camera.canvasToViewScale(e.getX() - cursor.getX(), e.getY() - cursor.getY());  // To avoid redundant calculations
                    for (GraphElement element : getHighlights()) {
                        if (element instanceof Vertex)
                            element.translateInView(delta);
                    }
                }
                else if (currentElement instanceof Vertex)
                    currentElement.translate(e.getX() - cursor.getX(), e.getY() - cursor.getY());
            }

            cursor = new Point2D(e.getX(), e.getY());
            dragged = true;
        });

        setOnMouseReleased(e -> {
            EventTarget target = e.getTarget();
            if (target != this && cursor != null && !dragged) {
                clearHighlights();
                addHighlight((GraphElement) ((Node) target).getParent());
            }
            cursor = null;
            dragged = false;
        });

        setOnScroll(e -> {
            if (e.isShortcutDown())
                camera.zoom(e.getDeltaY() / UNIT_SCROLL, e.getX(), e.getY());
            else
                camera.translate(-e.getDeltaX(), -e.getDeltaY());
        });
    }

    private void createVertex(double x, double y) {
        Vertex vertex = new Vertex(this, x, y);
        addElement(vertex);
    }

    private void createEdge(Vertex start, Vertex end) {
        Edge edge = new Edge(this, start, end, false);  // TODO: Modify this
        addElement(edge);
        edge.toBack();
    }

    public List<GraphElement> getElements() {
        return getChildren()
                .stream()
                .map(element -> (GraphElement) element)
                .toList();
    }
    public List<Vertex> getVertices() {
        return getChildren()
                .stream()
                .filter(element -> element instanceof Vertex)
                .map(element -> (Vertex) element)
                .toList();
    }
    public List<Edge> getEdges() {
        return getChildren()
                .stream()
                .filter(element -> element instanceof Edge)
                .map(element -> (Edge) element)
                .toList();
    }
    public void addElement(GraphElement element) {
        getChildren().add(element);
        clearHighlights();
        addHighlight(element);
    }

    public void removeElement(GraphElement element) {
        getChildren().remove(element);
        element.erase();
        clearHighlights();
    }

    public void removeElements(Collection<GraphElement> elements) {
        getChildren().removeAll(elements);
        elements.forEach(GraphElement::erase);
        clearHighlights();
    }

    public void clearElements() {
        getChildren().clear();
        clearHighlights();
    }

    public ObservableSet<GraphElement> getHighlights() {
        return unmodifiableHighlights;
    }
    public GraphElement getSingleHighlight() {
        Set<GraphElement> elements = getHighlights();
        return elements.size() == 1 ?
               elements.iterator().next() : null;
    }
    // Returns the awaiting vertex for edge formation, returns null if no or multiple vertices are highlighted
    private Vertex nomineeVertex() {
        return (getSingleHighlight() instanceof Vertex vertex) ?
               vertex : null;
    }
    public void addHighlight(GraphElement e) {
        highlights.add(e);
    }
    public void removeHighlight(GraphElement e) {
        highlights.remove(e);
    }
    public void clearHighlights() {
        highlights.clear();
    }
}
