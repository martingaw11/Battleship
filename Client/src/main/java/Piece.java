import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.util.ArrayList;

public class Piece {

    private double x, y;
    private int gridX, gridY, tileSize;
    public Rectangle r;
    boolean rotate, down;
    private String name;
    private ImagePattern image, imageUp;

    public Piece(double x, double y, int tileSize, Rectangle r, String name) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.tileSize = tileSize;
        this.name = name;
        gridX = -1;
        gridY = -1;
        rotate = false;
        down = false;
        image = new ImagePattern(new Image("images/" + name + ".png"));
        imageUp = new ImagePattern(new Image("images/" + name + "UP.png"));
    }

    public String getName() {
        return name;
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
            r.setFill(image);
        }else{
            r.setHeight(tileSize*40);
            r.setWidth(40);
            r.setFill(imageUp);
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