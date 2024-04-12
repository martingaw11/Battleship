import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;

public class Piece {

    private double x, y;
    private int gridX, gridY, tileSize;
    private Rectangle r;
    boolean rotate, down;

    public Piece(double x, double y, int tileSize, Rectangle r) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.tileSize = tileSize;
        gridX = -1;
        gridY = -1;
        rotate = false;
        down = false;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public void draw() {
        if (!rotate) {
            r.setWidth(tileSize*40);
            r.setHeight(40);
        }else{
            r.setHeight(tileSize*40);
            r.setWidth(40);
        }
        r.setTranslateX(x);
        r.setTranslateY(y);
    }

    public void setRotate(boolean rotate){
        this.rotate = rotate;
    }
    public boolean getRotate(){
        return rotate;
    }
    public int getTileSize(){
        return tileSize;
    }

    public void setDown(boolean down){
        this.down = down;
    }

    public boolean isDown(){
        return down;
    }
}