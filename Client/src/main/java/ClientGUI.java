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
            FXMLLoader loader = new FXMLLoader();
            Pane test = loader.load(Objects.requireNonNull(getClass().getResource("SnapToGrid.fxml")).openStream());
            BorderPane root = new BorderPane();
            root.setCenter(test);
            BorderPane.setAlignment(test, Pos.CENTER);
            primaryStage.setScene(new Scene(root));
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