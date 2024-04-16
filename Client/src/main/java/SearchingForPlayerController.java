import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class SearchingForPlayerController {
    @FXML
    public Pane paneToRotate;

    @FXML
    Button backToBase;

    @FXML
    TextField playerSearch;

    @FXML
    BorderPane rootSearch;

    Client clientConnection;

    @FXML
    public void initialize(){
        RotateTransition rt = new RotateTransition();
        rt.setDuration(Duration.millis(2000));
        rt.setToAngle(360);
        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.setNode(paneToRotate);
        rt.play();
        backToBase.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/SnapToGrid.fxml")));
                BorderPane temp = loader.load();
                SnapToGridController ctr = loader.getController();
                ctr.clientConnection = clientConnection;
                rootSearch.getScene().setRoot(temp);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
