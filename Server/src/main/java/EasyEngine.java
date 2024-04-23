import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

public class EasyEngine implements Engine{
    /*
        EASY ENGINE AI ALGORITHM
        ------------------------
        The Easy Engine is a simple implementation of the Engine interface.
        This AI Algorithm is supposed to be an easy opponent and will make
        very easy decision of where to shoot via a completely random guess.
     */

    // Tracking previously shot at locations
    private int[][] targetBoard;

    /**
     * Constructor of EasyEngine Object for Easy AI Algorithm
     */
    public EasyEngine() {
        targetBoard = new int[10][10];
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                targetBoard[row][col] = 0;
            }
        }
    }

    /**
     * Will make a random choice of a location to shoot at that has
     * not been targeted before.
     *
     * @param info information about the results of the previous move
     * @return Pair&lt;Integer, Integer&gt; which represents (x, y)
     *         coordinate location
     */
    @Override
    public Pair<Integer, Integer> makeMove(GameInfo info) {
        // initialize default's for (x, y) coordinate to strike
        int xCoord = -1, yCoord = -1;

        // at least once we will calculate a random value for x and y
        // 0 <= x <= 9   &&   0 <= y <= 9
        do {
            xCoord = (int)(Math.random() * 10);
            yCoord = (int)(Math.random() * 10);
            //System.out.println("Checking position x:" + xCoord + " y:" + yCoord);
        }
        // but need to recalculate if already targeted spot once
        while (targetBoard[yCoord][xCoord] == 1);

        // update targetBoard to reflect we just shot at board position
        targetBoard[yCoord][xCoord] = 1;

        // return never before hit coordinate we found
        return new Pair<>(xCoord, yCoord);
    }
}
