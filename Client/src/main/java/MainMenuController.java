import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Objects;

public class MainMenuController {
    @FXML
    Button pve, pvp, rules, exitGame, easy, medium, hard;

    @FXML
    BorderPane root;
    Client clientConnection;
    @FXML
    public void initialize(){
        pve.setOnAction(e -> {
            pve.setDisable(true);
            easy.setVisible(true);
            medium.setVisible(true);
            hard.setVisible(true);
        });
        pvp.setOnAction(e -> transitionScene(3));
        exitGame.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        rules.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/Rules.fxml")));
                Parent temp = loader.load();
                RulesController ctr = loader.getController();
                ctr.clientConnection = clientConnection;
                root.getScene().setRoot(temp);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        easy.setOnAction(e -> transitionScene(0));
        medium.setOnAction(e -> transitionScene(1));
        hard.setOnAction(e -> transitionScene(2));
    }

    public void transitionScene(int x){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/SnapToGrid.fxml")));
            Parent temp = loader.load();
            SnapToGridController ctr = loader.getController();
            ctr.clientConnection = clientConnection;
            clientConnection.difficulty = x;        // set engine mode
            ctr.userDisplay.setText(clientConnection.clientID);
            root.getScene().setRoot(temp);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
