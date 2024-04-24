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
import javafx.scene.layout.HBox;
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

    @FXML
    HBox missHitHbox;

    FadeTransition fade;

    ScaleTransition scale;

    PauseTransition pause, pauseAI;

    ImageView crosshair = new ImageView("images/crosshair.png");
    Image explosion = new Image("images/explosion.png");
    Image splash = new Image("images/splash.png");

    Pair<Integer, Integer> position =  null;

    private HashMap<String, Integer> shipSizeMap;

    private int healthPoints = 17;

    private HashMap<String, Text> graveYard;

    private boolean enableFire = false;

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
        scale.setOnFinished(e -> missHitHbox.toBack());

        fade = new FadeTransition();
        fade.setNode(hitMiss);
        fade.setDuration(Duration.millis(1000));
        fade.setInterpolator(Interpolator.LINEAR);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);
        fade.setOnFinished(e -> {
            if(enableFire){
                makeMove.setDisable(false);
                userTurn.setVisible(true);
                enemyTurn.setVisible(false);
            }
        });

        pause = new PauseTransition();
        pause.setDuration(Duration.seconds(2));
        pause.setOnFinished(e -> {
            backToBase();
        });

        pauseAI = new PauseTransition(Duration.millis(2500));
        pauseAI.setOnFinished(e -> {
            GameMessage aiFire = new GameMessage();
            aiFire.difficulty = clientConnection.difficulty;
            aiFire.userID = clientConnection.clientID;
            aiFire.operationInfo = "AI Fire";
            clientConnection.send(aiFire);
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
            surrenderMessage.userID = clientConnection.clientID;
            surrenderMessage.difficulty = clientConnection.difficulty;
            surrenderMessage.opponent = clientConnection.opponent;
            surrenderMessage.operationInfo = "Response";
            clientConnection.send(surrenderMessage);
            hitMiss.setText("YOU LOSE");
            missHitHbox.toFront();
            fade.play();
            scale.play();
            pause.play();
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
            fire.userID = clientConnection.clientID;
            fire.difficulty = clientConnection.difficulty;
            fire.opponent = clientConnection.opponent;
            fire.operationInfo = "Fire";
            fire.gameMove = new GameInfo();
            fire.gameMove.moveMade = position;
            clientConnection.send(fire);
            userTurn.setVisible(false);
            enemyTurn.setVisible(true);
        });
    }

    public void drawUser(boolean firstMove){
        shipSizeMap = new HashMap<>();
        graveYard = new HashMap<>();
        graveYard.put("AircraftCarrier", carrier);
        graveYard.put("BattleShip", battleship);
        graveYard.put("Destroyer", destroyer);
        graveYard.put("Submarine", submarine);
        graveYard.put("Cruiser", cruiser);
        for(Piece x : boatList){
            userPane.getChildren().add(x.r);
            x.r.setDisable(true);
            x.draw();
            shipSizeMap.put(x.getName(), x.getTileSize());
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
        if(position == null){
            enemyGrid.getChildren().add(crosshair);
        }
        currentPosition = (Button)actionEvent.getSource();
        position = new Pair<>(GridPane.getColumnIndex(currentPosition), GridPane.getRowIndex(currentPosition));
        GridPane.setConstraints(crosshair, position.getKey(), position.getValue());
        crosshair.toFront();
    }

    public GameMessage checkBoard(Pair<Integer, Integer> moveMade) {
        GameMessage tempMessage = new GameMessage();
        tempMessage.userID = clientConnection.clientID;
        tempMessage.difficulty = clientConnection.difficulty;
        tempMessage.opponent = clientConnection.opponent;
        tempMessage.operationInfo = "Response";
        tempMessage.gameMove = new GameInfo();
        tempMessage.gameMove.moveMade = moveMade;
        tempMessage.gameMove.shipHit = false;
        tempMessage.gameMove.shipSunk = false;

        ImageView image;

        for(String x : boatPositions.keySet()){
            for(Pair<Integer, Integer> y : boatPositions.get(x)){
                if(Objects.equals(y, moveMade)){
                    boatPositions.get(x).remove(y);
                    image = new ImageView("images/explosion.png");
                    GridPane.setConstraints(image, moveMade.getKey(), moveMade.getValue());
                    userGrid.getChildren().add(image);
                    image.toFront();
                    if(boatPositions.get(x).isEmpty()){
                        tempMessage.gameMove.shipSunk = true;
                        tempMessage.gameMove.currentShipHit = x;
                        tempMessage.gameMove.sizeOfShip = shipSizeMap.get(x);
                        hitMiss.setText("SHIP SUNK!");
                    }else{
                        hitMiss.setText("HIT");
                    }
                    tempMessage.gameMove.shipHit = true;
                    healthPoints--;
                    if(healthPoints == 0){
                        tempMessage.isOver = true;
                        tempMessage.userWon = false;
                        hitMiss.setText("YOU LOSE");
                        missHitHbox.toFront();
                        fade.play();
                        scale.play();
                        pause.play();
                        return tempMessage;
                    }
                    missHitHbox.toFront();
                    fade.play();
                    scale.play();
                    return tempMessage;
                }
            }
        }

        hitMiss.setText("MISS");
        missHitHbox.toFront();
        fade.play();
        scale.play();

        image = new ImageView("images/splash.png");
        GridPane.setConstraints(image, moveMade.getKey(), moveMade.getValue());
        userGrid.getChildren().add(image);
        image.toFront();

        return tempMessage;
    }

    public void responseHandling(GameMessage response) {
        aiTextHandling(response.turn, response.AI_Chat_Message);
        if(response.isOver){
            hitMiss.setText("YOU WIN");
            missHitHbox.toFront();
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
            GridPane.setConstraints(image, response.gameMove.moveMade.getKey(), response.gameMove.moveMade.getValue());
            enemyGrid.getChildren().add(image);
            image.toFront();
            if(response.gameMove.shipSunk){
                hitMiss.setText("SHIP SUNK!");
                graveYard.get(response.gameMove.currentShipHit).setStrikethrough(true);
            }else{
                hitMiss.setText("HIT");
            }
        }else{
            image = new ImageView("images/splash.png");
            GridPane.setConstraints(image, response.gameMove.moveMade.getKey(), response.gameMove.moveMade.getValue());
            enemyGrid.getChildren().add(image);
            image.toFront();
            hitMiss.setText("MISS");
        }
        missHitHbox.toFront();
        fade.play();
        scale.play();
        if(clientConnection.difficulty != 3)
            pauseAI.play();
    }

    public void backToBase(){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/SnapToGrid.fxml")));
            BorderPane temp = loader.load();
            SnapToGridController ctr = loader.getController();
            ctr.clientConnection = clientConnection;
            clientConnection.gameCtr = null;
            ctr.userDisplay.setText(clientConnection.clientID);
            rootGame.getScene().setRoot(temp);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void aiTextHandling(boolean turn, String str){
        enableFire = turn;
        final IntegerProperty i = new SimpleIntegerProperty(0);
        Timeline timeline = new Timeline();
        KeyFrame keyFrame = new KeyFrame(
                Duration.millis(50),
                event -> {
                    if (i.get() > str.length()) {
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
