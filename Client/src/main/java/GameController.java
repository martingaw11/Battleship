import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class GameController {

    public Client clientConnection;
    public Set<Piece> boatList;
    public HashMap<String, ArrayList<Pair<Integer, Integer>>> boatPositions;

    @FXML
    GridPane userGrid, enemyGrid;

    @FXML
    Pane userPane;

    Button currentPosition;

    @FXML
    BorderPane rootGame;

    @FXML
    Button forfeit, makeMove;

    Pair<Integer, Integer> position =  null;

    @FXML
    public void initialize(){
        forfeit.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/SnapToGrid.fxml")));
                BorderPane temp = loader.load();
                SnapToGridController ctr = loader.getController();
                ctr.clientConnection = clientConnection;
                rootGame.getScene().setRoot(temp);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void drawUser(){
        for(Piece x : boatList){
            userPane.getChildren().add(x.r);
            x.r.setDisable(true);
            x.draw();
            for(Pair<Integer, Integer> y : boatPositions.get(x.getName())){
                Rectangle r = new Rectangle(40, 40);
                r.setFill(Color.web("#FF6058"));
                r.setStroke(Color.CYAN);
                r.setStrokeWidth(3);
                GridPane.setConstraints(r, y.getKey(), y.getValue());
                userGrid.getChildren().add(r);
            }
        }
    }

    public void buttonHandler(ActionEvent actionEvent){
        if(currentPosition != null){
            currentPosition.setStyle("-fx-background-color: #11161A");
        }
        currentPosition = (Button)actionEvent.getSource();
        currentPosition.setStyle("-fx-background-color: #FF6058");
        position = new Pair<>(GridPane.getRowIndex(currentPosition), GridPane.getColumnIndex(currentPosition));
        System.out.println(position);
    }
}
