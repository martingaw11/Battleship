import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SearchingForPlayerController {
    @FXML
    public Pane paneToRotate;

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
    }
}
