import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import java.util.Objects;

public class ClientGUI extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            BorderPane test = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("SnapToGrid.fxml")));
            primaryStage.setScene(new Scene(test));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}