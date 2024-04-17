import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.util.Objects;

public class UsernameController {
    Client clientConnection;

    @FXML
    Button enterGame, exitGame;

    @FXML
    TextField usernameField, alertUser;

    @FXML
    BorderPane root;

    @FXML
    public void initialize(){
        enterGame.setOnAction(e -> {
            if(usernameField.getText().isEmpty()){
                alertUser.setText("Username can not be empty");
                return;
            }
            GameMessage trialRun = new GameMessage();
            trialRun.newUser = true;
            trialRun.userID = usernameField.getText();
            clientConnection.send(trialRun);
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/MainMenu.fxml")));
                Parent temp = loader.load();
                MainMenuController ctr = loader.getController();
                ctr.clientConnection = clientConnection;
                root.getScene().setRoot(temp);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        exitGame.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
    }
}
