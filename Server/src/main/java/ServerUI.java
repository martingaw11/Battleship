import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerUI extends Application {
    Server sv;
    ListView<String> screen = new ListView<>();

    public static void main(String[] arg){
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        sv = new Server(data->{
            Platform.runLater(() ->{
                screen.getItems().add(data.toString());
            });

        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setScene(createServerGui());

        primaryStage.show();
    }
    public Scene createServerGui() {


        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(70));
        pane.setStyle("-fx-background-color: coral");

        pane.setCenter(screen);
        pane.setStyle("-fx-font-family: 'serif'");


        return new Scene(pane, 500, 400);


    }
}
