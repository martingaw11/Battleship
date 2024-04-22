import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
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
    Button forfeit, makeMove, confirmButton, denyButton;

    @FXML
    TextField enemyFleet;

    @FXML
    Text userTurn, enemyTurn, carrier, battleship, submarine, cruiser, destroyer, hitMiss, AIText;

    FadeTransition fade;

    ScaleTransition scale;

    PauseTransition pause;

    ImageView crosshair = new ImageView("images/crosshair.png");
    Image explosion = new Image("images/explosion.png");
    Image splash = new Image("images/splash.png");

    Pair<Integer, Integer> position =  null;

    private HashMap<String, Integer> shipSizeMap;

    @FXML
    public void initialize(){
        scale = new ScaleTransition();
        scale.setNode(hitMiss);
        scale.setDuration(Duration.millis(1000));
        scale.setInterpolator(Interpolator.LINEAR);
        scale.setAutoReverse(true);
        scale.setByX(2.0);
        scale.setByY(2.0);
        scale.setCycleCount(2);

        fade = new FadeTransition();
        fade.setNode(hitMiss);
        fade.setDuration(Duration.millis(1000));
        fade.setInterpolator(Interpolator.LINEAR);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);

        pause = new PauseTransition();
        pause.setDuration(Duration.seconds(1));
        pause.setOnFinished(e -> {
            backToBase();
        });

        forfeit.setOnAction(e -> {
            forfeit.disarm();
            confirmButton.setVisible(true);
            denyButton.setVisible(true);
            forfeit.setText("Continue?");
        });
        confirmButton.setOnAction(e -> {
            GameMessage surrenderMessage = new GameMessage();
            surrenderMessage.isOver = true;
            surrenderMessage.userWon = false;
            surrenderMessage.opponent = clientConnection.opponent;
            surrenderMessage.operationInfo = "Response";
            clientConnection.send(surrenderMessage);
            backToBase();
        });
        denyButton.setOnAction(e -> {
            forfeit.arm();
            confirmButton.setVisible(false);
            denyButton.setVisible(false);
            forfeit.setText("Surrender");
        });
        makeMove.setOnAction(e -> {
            if(position == null){
                return;
            }
            makeMove.setDisable(true);
            GameMessage fire = new GameMessage();
            fire.opponent = clientConnection.opponent;
            fire.operationInfo = "Fire";
            fire.gameMove.moveMade = position;
            clientConnection.send(fire);
            userTurn.setVisible(false);
            enemyTurn.setVisible(true);
        });
    }

    public void drawUser(boolean firstMove){
        shipSizeMap = new HashMap<>();
        for(Piece x : boatList){
            userPane.getChildren().add(x.r);
            x.r.setDisable(true);
            x.draw();
            shipSizeMap.put(x.getName(), x.getTileSize());
            for(Pair<Integer, Integer> y : boatPositions.get(x.getName())){
                Rectangle r = new Rectangle(40, 40);
                r.setFill(Color.web("#FF6058"));
                r.setStroke(Color.CYAN);
                r.setStrokeWidth(3);
                GridPane.setConstraints(r, y.getKey(), y.getValue());
                userGrid.getChildren().add(r);
            }
        }
        if(clientConnection.difficulty == 3){
            enemyFleet.setText(clientConnection.opponent + "'s fleet:");
        }
        if(firstMove){
            userTurn.setVisible(true);
        }else{
            makeMove.setDisable(true);
            enemyTurn.setVisible(true);
        }
    }

    public void buttonHandler(ActionEvent actionEvent){
        if(currentPosition == null){
            enemyGrid.getChildren().add(crosshair);
        }
        currentPosition = (Button)actionEvent.getSource();
        position = new Pair<>(GridPane.getRowIndex(currentPosition), GridPane.getColumnIndex(currentPosition));
        GridPane.setConstraints(crosshair, position.getValue(), position.getKey());
        crosshair.toFront();
    }

    public GameMessage checkBoard(Pair<Integer, Integer> moveMade) {
        GameMessage tempMessage = new GameMessage();
        tempMessage.opponent = clientConnection.opponent;
        tempMessage.operationInfo = "Response";
        tempMessage.gameMove.moveMade = moveMade;
        tempMessage.gameMove.shipHit = false;
        tempMessage.gameMove.shipSunk = false;

        ImageView image;

        for(String x : boatPositions.keySet()){
            for(Pair<Integer, Integer> y : boatPositions.get(x)){
                if(Objects.equals(y, moveMade)){
                    boatPositions.get(x).remove(y);
                    image = new ImageView("images/explosion.png");
                    GridPane.setConstraints(image, moveMade.getValue(), moveMade.getKey());
                    image.setFitWidth(40);
                    image.setFitHeight(40);
                    userGrid.getChildren().add(image);
                    image.toFront();
                    if(boatPositions.get(x).isEmpty()){
                        tempMessage.gameMove.shipSunk = true;
                        tempMessage.gameMove.currentShipHit = x;
                        tempMessage.gameMove.sizeOfShip = shipSizeMap.get(x);
                        tempMessage.isOver = true;
                        tempMessage.userWon = false;
                        hitMiss.setText("YOU LOSE");
                        pause.play();
                    }else{
                        hitMiss.setText("HIT");
                    }
                    tempMessage.gameMove.shipHit = true;
                    fade.play();
                    scale.play();
                    return tempMessage;
                }
            }
        }

        hitMiss.setText("MISS");
        fade.play();
        scale.play();

        image = new ImageView("images/splash.png");
        GridPane.setConstraints(image, moveMade.getValue(), moveMade.getKey());
        image.setFitWidth(40);
        image.setFitHeight(40);
        userGrid.getChildren().add(image);
        image.toFront();

        return tempMessage;
    }

    public void responseHandling(GameMessage response) {
        aiTextHandling(false, response.AI_Chat_Message);
        if(response.isOver){
            hitMiss.setText("YOU WIN");
            fade.play();
            scale.play();
            pause.play();
            return;
        }
        if(Objects.equals(response.gameMove.moveMade, position)){
            position = null;
            enemyGrid.getChildren().remove(crosshair);
        }
        ImageView image;
        if(response.gameMove.shipHit){
            image = new ImageView("images/explosion.png");
            GridPane.setConstraints(image, response.gameMove.moveMade.getValue(), response.gameMove.moveMade.getKey());
            image.setFitWidth(40);
            image.setFitHeight(40);
            enemyGrid.getChildren().add(image);
            image.toFront();
            if(response.gameMove.shipSunk){
                hitMiss.setText(response.gameMove.currentShipHit + " SUNK!");
            }else{
                hitMiss.setText("HIT");
            }
        }else{
            image = new ImageView("images/splash.png");
            GridPane.setConstraints(image, response.gameMove.moveMade.getValue(), response.gameMove.moveMade.getKey());
            image.setFitWidth(40);
            image.setFitHeight(40);
            enemyGrid.getChildren().add(image);
            image.toFront();
            hitMiss.setText("MISS");
        }
        fade.play();
        scale.play();
    }

    public void backToBase(){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/SnapToGrid.fxml")));
            BorderPane temp = loader.load();
            SnapToGridController ctr = loader.getController();
            ctr.clientConnection = clientConnection;
            clientConnection.gameCtr = null;
            rootGame.getScene().setRoot(temp);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void aiTextHandling(boolean turn, String str){
        final IntegerProperty i = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(50),
                event -> {
                    if (i.get() > str.length()) {
                        if(turn){
                            makeMove.setDisable(false);
                            userTurn.setVisible(true);
                            enemyTurn.setVisible(false);
                        }
                        timeline.stop();
                    } else {
                        AIText.setText(str.substring(0, i.get()));
                        i.set(i.get() + 1);
                    }
                });
        timeline.getKeyFrames().add(keyFrame);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
