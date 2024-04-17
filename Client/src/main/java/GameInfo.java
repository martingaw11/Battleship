import javafx.util.Pair;

import java.util.ArrayList;

public class GameInfo {
    /*
        GAME INFORMATION STRUCTURE
        --------------------------
        This Object will carry the vital information necessary for both
        players to process and request game moves made for the duration
        of the game.
     */

    Pair<Integer, Integer> moveMade;
    /**
     * returns true if ship was hit
     */
    boolean shipHit;
    /**
     * returns true when a hit causes a ship to sink
     */
    boolean shipSunk;
    /**
     * returns size of ship was hit
     */
    int sizeOfShip;
    /**
     * returns total number of ship hit
     */
    int totalShipHit;
    /**
     * returns total number of ship sunk
     */
    int totalShipSunk;
    /**
     * returns name of ship hit
     */
    String currentShipHit;
    /**
     * ArrayList<string>  of ships sunk
     */
    ArrayList<String> graveYard;
}
