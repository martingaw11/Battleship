import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
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
    Pair<Integer, Integer> position =  null;

    @FXML
    public void initialize(){
    }

    public void drawUser(){
        for(Piece x : boatList){
            userPane.getChildren().add(x.r);
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
            currentPosition.setStyle("-fx-background-color: lightblue");
        }
        currentPosition = (Button)actionEvent.getSource();
        currentPosition.setStyle("-fx-background-color: #FF6058");
        position = new Pair<>(GridPane.getRowIndex(currentPosition), GridPane.getColumnIndex(currentPosition));
        System.out.println(position);
    }
}
