import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ServerUI extends Application {
    Server sv;

    public static void main(String[] arg){
        launch(arg);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        sv = new Server(data->{
            Platform.runLater(() ->{
                System.out.println(data.toString());
            });
        });
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
}
