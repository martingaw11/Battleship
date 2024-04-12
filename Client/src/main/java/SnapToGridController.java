import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class SnapToGridController {

    @FXML
    Pane pane;

    @FXML
    BorderPane root;

    @FXML
    Button resetButton, shuffleButton, deployFleet;

    private int size = 400;
    private int spots = 10;
    private int squareSize = size / spots;

    private HashMap<Piece, Pair<Integer, Integer>> boatList;
    private Rectangle[][] grid;
    private boolean rotateMode = false;

    @FXML
    public void initialize() {
        grid = new Rectangle[spots][spots];
        for (int i = 0; i < size; i += squareSize) {
            for (int j = 0; j < size; j += squareSize) {
                Rectangle r = new Rectangle(i, j, squareSize, squareSize);
                grid[i / squareSize][j / squareSize] = r;
                r.setFill(Color.LIGHTSKYBLUE);
                r.setStroke(Color.DARKBLUE);
                r.setStrokeWidth(3);
                r.setOpacity(0.5);
                pane.getChildren().add(r);
            }
        }
        boatList = new HashMap<>();
        int[] boatSizes = {5, 4, 3, 3, 2};
        for (int i = 0; i < 5; i++) {
            Rectangle r = new Rectangle();
            r.setFill(Color.GREEN);
            r.setStroke(Color.BLACK);
            r.setOpacity(0.2);
            int tileSize = boatSizes[i];
            int x = 528 + (5-tileSize)*20;
            int y = 27 + i*80;
            Piece p = new Piece(x, y, tileSize, r);
            boatList.put(p, new Pair<>(x, y));

            r.setOnMousePressed(event -> pressed(event, p));
            r.setOnMouseDragged(event -> dragged(event, p));
            r.setOnMouseReleased(event -> released(event, p));
            r.setOnMouseEntered(e -> pane.setCursor(Cursor.OPEN_HAND));
            r.setOnMouseExited(e -> pane.setCursor(Cursor.DEFAULT));

            pane.getChildren().add(r);
            p.draw();
        }

        // Set up event handler for key released
        pane.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.R) {
                rotateMode = !rotateMode;
            }
        });

        resetButton.setOnAction(e -> {
            resetButton.setDisable(true);
            for(Piece toClear : boatList.keySet()){
                clearPreviousPosition(toClear);
                resetPosition(toClear);
            }
            resetButton.setDisable(false);
        });
        shuffleButton.setOnAction(e -> {
            shuffleButton.setDisable(true);
            for(Piece toClear : boatList.keySet()){
                clearPreviousPosition(toClear);
                resetPosition(toClear);
            }
            for(Piece toShuffle : boatList.keySet()){
                shuffleBoats(toShuffle);
            }
            shuffleButton.setDisable(false);
        });
        deployFleet.setOnAction(e -> {
            try {
                BorderPane temp = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("SearchingForPlayer.fxml")));
                root.getScene().setRoot(temp);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Ensure pane is focused to receive key events
        pane.setFocusTraversable(true);
    }

    public void pressed(MouseEvent event, Piece p) {
        rotateMode = p.getRotate();
        pane.setCursor(Cursor.CLOSED_HAND);
    }

    public void dragged(MouseEvent event, Piece p) {
        p.setRotate(rotateMode);
        double tempX, tempY;
        if(!p.getRotate()){
            tempX = p.getX() + event.getX() - 20 * p.getTileSize();
            tempY = p.getY() + event.getY() - 20;
        }else{
            tempY = p.getY() + event.getY() - 20 * p.getTileSize();
            tempX = p.getX() + event.getX() - 20;
        }
        if (tempX < 0) {
            p.setX(0);
        } else if (tempX + 40 * ((p.getRotate())? 1 : p.getTileSize()) > pane.getWidth()) {
            p.setX(pane.getWidth()-40*((p.getRotate())? 1 : p.getTileSize()));
        } else {
            p.setX(tempX);
        }
        if (tempY < 0) {
            p.setY(0);
        } else if (tempY + 40*((!p.getRotate())? 1 : p.getTileSize()) > pane.getHeight()) {
            p.setY(pane.getHeight()-40*((!p.getRotate())? 1 : p.getTileSize()));
        } else {
            p.setY(tempY);
        }
        p.draw();
    }

    public void released(MouseEvent event, Piece p) {
        int gridx;
        int gridy;
        if(!p.getRotate()){
            gridx = (int)((p.getX() + 40 * p.getTileSize() / 2) /squareSize - p.getTileSize()/2);
            gridy = (int)(p.getY() + 20) / squareSize;
        }else{
            gridy = (int)((p.getY() + 40 * p.getTileSize() / 2) /squareSize - p.getTileSize()/2);
            gridx = (int)(p.getX() + 20) / squareSize;
        }
        clearPreviousPosition(p);
        ArrayList<Pair<Integer, Integer>> gridPosTemp = new ArrayList<>();
        if(!p.getRotate()){
            if(gridx > 10-p.getTileSize() || gridy > 9 || gridx<0 || gridy < 0){
                resetPosition(p);
                pane.setCursor(Cursor.OPEN_HAND);
                return;
            }
            for(int i = 0; i < p.getTileSize(); i++){
                if(!grid[gridx+i][gridy].getFill().equals(Color.CRIMSON))
                    gridPosTemp.add(new Pair<>(gridx+i, gridy));
                else {
                    resetPosition(p);
                    gridPosTemp.clear();
                    pane.setCursor(Cursor.OPEN_HAND);
                    return;
                }
            }
            p.setDown(false);
        }else{
            if(gridx > 9 || gridy > 10-p.getTileSize() || gridx<0 || gridy < 0){
                resetPosition(p);
                pane.setCursor(Cursor.OPEN_HAND);
                return;
            }
            for(int i = 0; i < p.getTileSize(); i++){
                if(!grid[gridx][gridy+i].getFill().equals(Color.CRIMSON))
                    gridPosTemp.add(new Pair<>(gridx, gridy+i));
                else {
                    resetPosition(p);
                    gridPosTemp.clear();
                    pane.setCursor(Cursor.OPEN_HAND);
                    return;
                }
            }
            p.setDown(true);
        }
        for(Pair<Integer, Integer> gridPos : gridPosTemp){
            grid[gridPos.getKey()][gridPos.getValue()].setFill(Color.CRIMSON);
        }
        gridPosTemp.clear();
        p.setX(squareSize * gridx);
        p.setY(squareSize * gridy);
        p.setGridX(gridx);
        p.setGridY(gridy);
        p.draw();
        pane.setCursor(Cursor.OPEN_HAND);
    }

    private void resetPosition(Piece p) {
        p.setX(boatList.get(p).getKey());
        p.setY(boatList.get(p).getValue());
        p.setDown(false);
        p.setRotate(false);
        p.setGridX(-1);
        p.setGridY(-1);
        p.draw();
    }

    private void clearPreviousPosition(Piece p) {
        if(p.getGridY() != -1){
            if(p.isDown())
                for(int i = 0; i < p.getTileSize(); i++)
                    grid[p.getGridX()][p.getGridY()+i].setFill(Color.LIGHTSKYBLUE);
            else
                for(int i = 0; i < p.getTileSize(); i++)
                    grid[p.getGridX()+i][p.getGridY()].setFill(Color.LIGHTSKYBLUE);
        }
    }

    private void shuffleBoats(Piece p){
        int gridx = 0;
        int gridy = 0;
        ArrayList<Pair<Integer, Integer>> gridPosTemp = null;
        while(true) {
            p.setRotate(Math.random() < 0.5);
            gridx = (int) (Math.random() * 10) %10;
            gridy = (int) (Math.random() * 10) %10;
            clearPreviousPosition(p);
            gridPosTemp = new ArrayList<>();
            if(!p.getRotate()){
                if(gridx > 10-p.getTileSize() || gridy > 9 || gridx<0 || gridy < 0){
                    continue;
                }
                for(int i = 0; i < p.getTileSize(); i++){
                    if(!grid[gridx+i][gridy].getFill().equals(Color.CRIMSON))
                        gridPosTemp.add(new Pair<>(gridx+i, gridy));
                    else {
                        gridPosTemp.clear();
                        break;
                    }
                }
                if(gridPosTemp.isEmpty())
                    continue;
                p.setDown(false);
            }else{
                if(gridx > 9 || gridy > 10-p.getTileSize() || gridx<0 || gridy < 0){
                    continue;
                }
                for(int i = 0; i < p.getTileSize(); i++){
                    if(!grid[gridx][gridy+i].getFill().equals(Color.CRIMSON))
                        gridPosTemp.add(new Pair<>(gridx, gridy+i));
                    else {
                        gridPosTemp.clear();
                        break;
                    }
                }
                if(gridPosTemp.isEmpty())
                    continue;
                p.setDown(true);
            }
            break;
        }
        for(Pair<Integer, Integer> gridPos : gridPosTemp){
            grid[gridPos.getKey()][gridPos.getValue()].setFill(Color.CRIMSON);
        }
        gridPosTemp.clear();
        p.setX(squareSize * gridx);
        p.setY(squareSize * gridy);
        p.setGridX(gridx);
        p.setGridY(gridy);
        p.draw();
    }

}