import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Objects;

public class ClientGUI extends Application {
    Client clientConnection;
    @Override
    public void start(Stage primaryStage) {
        clientConnection = new Client(data->{
            if(((Message)data).newUser){
                System.out.println(((Message) data).userID);
            }
            Platform.runLater(() ->{
                System.out.println(data);
            });
        });
        clientConnection.start();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/Username.fxml")));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            UsernameController ctr = loader.getController();
            ctr.clientConnection = clientConnection;
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