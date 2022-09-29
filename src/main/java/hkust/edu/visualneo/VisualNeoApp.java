package hkust.edu.visualneo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class VisualNeoApp extends Application {

    QueryHandler queryHandler;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(VisualNeoApp.class.getResource("fxml/visualneo-home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("VisualNeo");
        stage.setScene(scene);
        stage.show();

        queryHandler = new QueryHandler(this);
    }

    public static void main(String[] args) {
        launch();
    }
}