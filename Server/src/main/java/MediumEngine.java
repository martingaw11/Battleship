import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class MediumEngine implements Engine {
    /*
        MEDIUM ENGINE AI ALGORITHM
        --------------------------
        The Medium Engine is an improved version of the Easy Engine implementation.
        Improvements to the algorithm for picking spots to shoot in Battleship include:
        1) Taking Advantage of Known Parity
            -- The minimum piece size in Battleship is 2, we can recognize that we can
               forget about half the options on the board when shooting randomly. If we
               only ever shoot at "even" squares (same goes to shooting only "odd" squares)
               then we are guaranteed to hit each ship at least once, which leads to...

        2) Targeting a Ship that has been Hit
            -- The player has the privilege of knowing whether they hit a piece or not,
               so the AI Algorithm should get the same privilege. With this knowledge,
               the algorithm can instead target a ship it just hit to try to sink it by
               continuously targeting locations around successful hits.

        Combining these two ideas into a new algorithm, we greatly improve AI performance.
     */

    // keeping track of places missed(1)/hit(2)/not-shot-at(0), where board is technically Row-by-Col which is Y-by-X
    //ArrayList<ArrayList<Integer>> targetBoard;
    int[][] targetBoard;

    // if not empty (target mode activated), stack of all possible moves
    Stack<Pair<Integer, Integer>> possibleMoves;

    // last move engine made
    // lastMoveMade is local to MediumEngine and HardEngine, therefore cannot be accessible from Engine Interface
    Pair<Integer, Integer> lastMoveMade;

    /**
     * Constructor of MediumEngine Object for Medium AI Algorithm
     */
    public MediumEngine() {
        // create the default target board with no shots fired
//        targetBoard = new ArrayList<>();
//        // create a row of 10 zeros which will be added 10 times to target board
//        ArrayList<Integer> zeroRow = new ArrayList<>(Collections.nCopies(10, 0));
//        for (int i = 0; i < 10; i++) {
//            targetBoard.add(zeroRow);
//        }

        targetBoard = new int[10][10];
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                targetBoard[row][col] = 0;
            }
        }

        // empty initialization for possible move stack
        possibleMoves = new Stack<>();
    }

    /**
     * Will make a random move with parity (that has not been guessed previously) if engine is
     * currently not targeting a ship, else since engine is targeting a ship it will make a
     * move trying to sink current ship.
     *
     * @param info information about the results of the previous move
     * @return Pair&lt;Integer, Integer&gt; which represents (x, y) coordinate location
     */
    @Override
    public Pair<Integer, Integer> makeMove(GameInfo info) {
        // if info is null, then first move needs to be made
        if (info == null) {
            lastMoveMade = parityGuess();
            return lastMoveMade;
        }

        // if hit a ship, mark spot as hit and add valid neighboring cells not shot at to the possible move stack
        if (info.shipHit) {
            //targetBoard.get(lastMoveMade.getValue()).set(lastMoveMade.getKey(), 2);
            targetBoard[lastMoveMade.getKey()][lastMoveMade.getValue()] = 2;
            prepareShots(lastMoveMade);
        }
        // mark location shot at previously as missed
        else {
            //targetBoard.get(lastMoveMade.getValue()).set(lastMoveMade.getKey(), 1);
            targetBoard[lastMoveMade.getKey()][lastMoveMade.getValue()] = 1;
        }

        // if possible move stack is not empty, engine is in target mode, pop targeted move off of stack
        if (!possibleMoves.empty()) {
            // make sure to store move for next shot selection
            lastMoveMade = possibleMoves.peek();
            while (targetBoard[lastMoveMade.getKey()][lastMoveMade.getValue()] != 0) {
                possibleMoves.pop();
                lastMoveMade = possibleMoves.peek();
            }
            return possibleMoves.pop();
        }
        // else, make a random guess with parity
        else {
            // make sure to store move for next shot selection
            lastMoveMade = parityGuess();
            return lastMoveMade;
        }
    }

    /**
     * Taking advantage of parity, will only guess Even columns in Even rows or Odd columns
     * in Odd rows [i.e. (0,0) (0,4) (2,8) (1,5) (9,9) (7,3)]
     *
     * @return Pair&lt;Integer, Integer&gt; which represents (x, y) coordinate location
     */
    private Pair<Integer, Integer> parityGuess() {
        // set up x and y coordinate defaults before randomized guess
        int xCoord = -1, yCoord = -1;

        do {
            // first calculate row that will be chosen 0 <= y <= 9
            yCoord = (int)(Math.random() * 10);
            // xCoord will always start as random number 0 <= x <= 4
            xCoord = (int)(Math.random() * 5);

            // assume xCoord to be Even, since Odd or Even xCoord needs to be multiplied by 2
            // for both the conversions to Even and odd
            xCoord *= 2;

            // if xCoord is Odd, transform xCoord to be Odd in range 0 < x <= 9
            if (yCoord % 2 != 0) {
                xCoord += 1;
            }
            System.out.println("Checking position x:" + xCoord + " y:" + yCoord);
        }
        // loop until we find spot that has not been shot at yet
        //while (targetBoard.get(xCoord).get(yCoord) != 0);
        while (targetBoard[xCoord][yCoord] != 0);

        // finally found coordinates that hasn't been shot at, return this
        return new Pair<Integer, Integer>(xCoord, yCoord);
    }

    /**
     * Given a shot (assuming it was a hit), add all valid neighbors that have not been
     * shot at yet to the possible shot stack
     *
     * @param rootShot the shot that has hit a ship on the board
     */
    private void prepareShots(Pair<Integer, Integer> rootShot) {
        int yCoord = rootShot.getValue();
        int xCoord = rootShot.getKey();

        // if [y-1][x] is on grid and hasn't been shot at, add it to shot stack
        if (xCoord > 0 && targetBoard[xCoord-1][yCoord] == 0) {
            possibleMoves.add(new Pair<>(xCoord-1, yCoord));
        }

        // if [y+1][x] is on grid and hasn't been shot at, add it to shot stack
        if (xCoord+1 < targetBoard.length && targetBoard[xCoord+1][yCoord] == 0) {
            possibleMoves.add(new Pair<>(xCoord+1, yCoord));
        }

        // if [y][x-1] is on grid and hasn't been shot at, add it to shot stack
        if (yCoord > 0 && targetBoard[xCoord][yCoord-1] == 0) {
            possibleMoves.add(new Pair<>(xCoord, yCoord-1));
        }

        // if [y][x+1] is on grid and hasn't been shot at, add it to shot stack
        if (yCoord+1 < targetBoard[xCoord].length && targetBoard[xCoord][yCoord+1] == 0) {
            possibleMoves.add(new Pair<>(xCoord, yCoord+1));
        }
    }
}
