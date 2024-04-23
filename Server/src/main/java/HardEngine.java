import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class HardEngine implements Engine {
    /*
        HARD ENGINE AI ALGORITHM
        ------------------------
        The Hard Engine is an improved version of the Medium Engine implementation.
        Improvements to the algorithm to better pick spots to shoot at include:
        1) Tracking the Ships Left to Sink:
            -- By keeping track of the ships left to sink, the engine can use the largest
               ship left to calculate the probability of the largest ship appearing at
               all the locations (not yet shot at) on the board.

        2) Converting Hit Ships to Sunken Ships on Target Board:
            -- Once target mode is over, and it is only over once all ships hit have
               been sunk, then we treat hit ships as sunken ships. This improves the
              effectiveness of calculating ship location probability by making sure to
              avoid overlap with locations that already have a sunken ship.

        3) Calculating Probability rather than Random Guess:
            -- Instead of just making a random guess, or even a random guess with parity,
               the engine will be using probability to make its hunting guess. This means
               that anytime the engine is search for a ship hit, it will be making the most
               likely moves to result in a hit. This greatly improves performance by reducing
               randomness with a statistical guess.

         Combining these three improvements greatly increases the algorithm performance.
     */

    // keeping track of places missed(1)/hit(2)/not-shot-at(0), where board is technically Row-by-Col which is Y-by-X
    ArrayList<ArrayList<Integer>> targetBoard;

    // keep track of pieces left, from largest to smallest, so that calculate probability based on largest piece left
    ArrayList<Integer> piecesLeft;

    // the stack of all possible moves left to use up in target mode
    Stack<Pair<Integer, Integer>> possibleMoves;

    // storing the last move that the engine made for the game
    // lastMoveMade is local to MediumEngine and HardEngine, therefore cannot be accessible from Engine Interface
    Pair<Integer, Integer> lastMoveMade;

    /**
     * Constructor of HardEngine Object for Hard AI Algorithm
     */
    public HardEngine() {
        // create the default target board with no shots fired
        targetBoard = new ArrayList<>();
        // create a row of 10 zeros which will be added 10 times to target board
        ArrayList<Integer> zeroRow = new ArrayList<>(Collections.nCopies(10, 0));
        for (int i = 0; i < 10; i++) {
            targetBoard.add(zeroRow);
        }

        // must initialize pieces left from largest to smallest
        piecesLeft = new ArrayList<>(Arrays.asList(5, 4, 3, 3, 2));

        // empty initialization of possible move stack
        possibleMoves = new Stack<>();
    }

    /**
     * Will hunt for a hit on a ship using probability to calculate the best guess, once
     * a hit is achieved then in target mode it will make a targeted move to try and sink
     * the ship that has been hit.
     *
     * @param info information about the results of the previous move
     * @return Pair&lt;Integer, Integer&gt; which represents (x, y) coordinate location
     */
    @Override
    public Pair<Integer, Integer> makeMove(GameInfo info) {
        // no game info feedback means first move needs to be made, default is bestGuess
        if (info == null) {
            lastMoveMade = calculateBestGuess();
            return lastMoveMade;
        }

        // if hit a ship, mark spot as hit and add valid neighboring cells not shot at to the possible move stack
        if (info.shipHit) {
            targetBoard.get(lastMoveMade.getValue()).set(lastMoveMade.getKey(), 2);
            prepareShots(lastMoveMade);
        }
        // mark location shot at previously as missed
        else {
            targetBoard.get(lastMoveMade.getValue()).set(lastMoveMade.getKey(), 1);
        }

        // if ship has been sunk on last move, remove ship size from piecesLeft
        if (info.shipSunk) {
            piecesLeft.remove((Integer) info.sizeOfShip);
        }

        // if possible move stack is not empty, engine is in target mode, pop targeted move off of stack
        if (!possibleMoves.empty()) {
            // make sure to store move for next shot selection
            lastMoveMade = possibleMoves.peek();
            return possibleMoves.pop();
        }
        // else, make a random guess with parity
        else {
            // make sure to store move for next shot selection
            lastMoveMade = calculateBestGuess();
            return lastMoveMade;
        }
    }

    /**
     * Will use statistics and calculate probability of the largest ship appearing on each
     * cell location, it will return the cell location with the highest probability of
     * having the largest ship located on it.
     *
     * @return Pair&lt;Integer, Integer&gt; which represents (x, y) coordinate location
     */
    private Pair<Integer, Integer> calculateBestGuess() {
        // Idea for this short process is to create a grid representing target board
        // Iteratively it will check which positions the ship could be in on the target board
        // then for a valid position each cell will have their value incremented by 1
        // The cell with the greatest integer value will be most likely location

        // initialize 10x10 grid and get the largest ship size from the pieces left
        int[][] probabilityGrid = new int[10][10];
        int shipSize = piecesLeft.get(0);

        int xCoord = -1, yCoord = -1;
        int maxProbability = 0;

        // first need to check all horizontal orientations (always facing right) of the ship
        for (int row = 0; row < 10; row++) {
            // can't check all columns as starting point of ship will be off of map
            for (int col = 0; col < 11-shipSize; col++) {
                boolean valid = true;
                // check to make sure each spot is a valid space for the ship to be
                for (int i = 0; i < shipSize; i++) {
                    if (targetBoard.get(row+i).get(col) != 0) {
                        valid = false;
                        break;
                    }
                }
                // then loop over same spots again if it is a valid location to increment probability
                if (valid) {
                    for (int i = 0; i < shipSize; i++) {
                        probabilityGrid[row+i][col]++;
                        // update current max probability location if location is more probable
                        if (probabilityGrid[row+i][col] > maxProbability) {
                            maxProbability = probabilityGrid[row+i][col];
                            yCoord = row+i;
                            xCoord = col;
                        }
                    }
                }

            }
        }

        // second need to check all vertical orientations (always facing down) of the ship
        // can't check all rows as starting point of ship will be off of map
        for (int row = 0; row < 11-shipSize; row++) {
            for (int col = 0; col < 10; col++) {
                boolean valid = true;
                // check to make sure each spot is a valid space for the ship to be
                for (int j = 0; j < shipSize; j++) {
                    if (targetBoard.get(row).get(col+j) != 0) {
                        valid = false;
                        break;
                    }
                }
                // then loop over same spots again if it is a valid location to increment probability
                if (valid) {
                    for (int j = 0; j < shipSize; j++) {
                        probabilityGrid[row][col+j]++;
                        // update current max probability location if location is more probable
                        if (probabilityGrid[row][col+j] > maxProbability) {
                            maxProbability = probabilityGrid[row][col+j];
                            yCoord = row;
                            xCoord = col+j;
                        }
                    }
                }
            }
        }

        return new Pair<>(xCoord, yCoord);
    }

    /**
     * Using the last shot that just hit a ship, this will add all valid shot locations
     * that have not been fired at yet to the possible shot stack.
     *
     * @param rootShot the shot that just hit a ship on the board
     */
    private void prepareShots(Pair<Integer, Integer> rootShot) {
        int yCoord = rootShot.getValue();
        int xCoord = rootShot.getKey();

        // if [y-1][x] is on grid and hasn't been shot at, add it to shot stack
        if (yCoord > 0 && targetBoard.get(yCoord-1).get(xCoord) == 0) {
            possibleMoves.add(new Pair<>(yCoord-1, xCoord));
        }

        // if [y+1][x] is on grid and hasn't been shot at, add it to shot stack
        if (yCoord+1 < targetBoard.size() && targetBoard.get(yCoord+1).get(xCoord) == 0) {
            possibleMoves.add(new Pair<>(yCoord+1, xCoord));
        }

        // if [y][x-1] is on grid and hasn't been shot at, add it to shot stack
        if (xCoord > 0 && targetBoard.get(yCoord).get(xCoord-1) == 0) {
            possibleMoves.add(new Pair<>(yCoord, xCoord-1));
        }

        // if [y][x+1] is on grid and hasn't been shot at, add it to shot stack
        if (xCoord+1 < targetBoard.get(yCoord).size() && targetBoard.get(yCoord).get(xCoord+1) == 0) {
            possibleMoves.add(new Pair<>(yCoord, xCoord+1));
        }
    }
}
