import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class RulesController {

    @FXML
    Button backToMainMenu;

    @FXML
    BorderPane rootRules;

    Client clientConnection;

    @FXML
    public void initialize(){
        backToMainMenu.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/MainMenu.fxml")));
                BorderPane temp = loader.load();
                MainMenuController ctr = loader.getController();
                ctr.clientConnection = clientConnection;
                rootRules.getScene().setRoot(temp);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

}
