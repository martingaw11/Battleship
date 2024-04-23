import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class ClientGUI extends Application {
    Client clientConnection;

    PauseTransition goToGame = new PauseTransition(Duration.seconds(1));

    GameMessage handledMessage;

    BorderPane tempRoot;
    @Override
    public void start(Stage primaryStage) {
        clientConnection = new Client(data->{
            handledMessage = (GameMessage)data;
            // todo: how is pve start game handled
            if(handledMessage.opponentMatched){
                clientConnection.opponent = handledMessage.opponent;
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/Game.fxml")));
                    tempRoot = loader.load();
                    GameController ctr = loader.getController();
                    ctr.clientConnection = clientConnection;
                    ctr.boatList = clientConnection.searchingCtr.boatList;
                    ctr.boatPositions = clientConnection.searchingCtr.boatPositions;
                    ctr.drawUser(handledMessage.makeFirstMove);
                    clientConnection.gameCtr = ctr;
                    clientConnection.searchingCtr.playerSearch.setText("Opponent found: " + handledMessage.opponent);
                    goToGame.play();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else if (handledMessage.operationInfo.equals("Fire")) {
                Platform.runLater(() -> clientConnection.send(clientConnection.gameCtr.checkBoard(handledMessage.gameMove.moveMade)));
            } else if (handledMessage.operationInfo.equals("Response")) {     //todo: we need to check if the message object was from the same client
                                                                            // todo:  ifso update opponent side of the board
                Platform.runLater(() -> clientConnection.gameCtr.responseHandling(handledMessage));
            } else if (handledMessage.operationInfo.equals("AI Message")) {
                // This is meant to enable the fire button after you get the AI message back when you respond to a fire.
                // So like the server sends back an appropriate AI message after checking the response and that enables you to fire
                Platform.runLater(() -> clientConnection.gameCtr.aiTextHandling(true, handledMessage.AI_Chat_Message));
            } else if(handledMessage.newUser && clientConnection.clientID == null){
                clientConnection.userNames.addAll(handledMessage.userNames);
            }
        });
        clientConnection.start();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        goToGame.setOnFinished(e -> {
            clientConnection.searchingCtr = null;
            primaryStage.getScene().setRoot(tempRoot);
            Platform.runLater(() -> clientConnection.gameCtr.aiTextHandling(handledMessage.makeFirstMove, handledMessage.AI_Chat_Message));
        });
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("fxml/Username.fxml")));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            UsernameController ctr = loader.getController();
            ctr.clientConnection = clientConnection;
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}