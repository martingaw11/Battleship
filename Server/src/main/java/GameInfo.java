import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class GameInfo implements Serializable {
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
    boolean shipHit = false;
    /**
     * returns true when a hit causes a ship to sink
     */
    boolean shipSunk = false;
    /**
     * returns size of ship was hit
     */
    int sizeOfShip;
    /**
     * returns name of ship hit
     */
    String currentShipHit;
   ;
}
