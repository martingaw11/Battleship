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
        pvp.setOnAction(e -> { // TEMPORARY so you can use both buttons to go to the next scene, there will be a separate scene for bot difficulty choice don't worry
            transitionScene();
        });
        exitGame.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        easy.setOnAction(e -> transitionScene());
        medium.setOnAction(e -> transitionScene());
        hard.setOnAction(e -> transitionScene());
    }

    public void transitionScene(){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/SnapToGrid.fxml")));
            Parent temp = loader.load();
            SnapToGridController ctr = loader.getController();
            ctr.clientConnection = clientConnection;
            root.getScene().setRoot(temp);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
