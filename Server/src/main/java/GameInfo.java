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
    boolean shipHit;
    boolean shipSunk;
    int sizeOfShip;
    int totalShipHit;
    int totalShitSunk;
    String currentShipHit;
    ArrayList<String> graveYard;
}
