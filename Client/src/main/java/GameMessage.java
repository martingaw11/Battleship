import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class GameMessage implements Serializable {
    static final long serialVersionUID = 42L;
    /**
     * returns true if user is new to server
     */
    boolean newUser = false;
    /**
     * returns true if user starts game round in player VS player mode
     */
    boolean makeFirstMove = false;
    /**
     * returns true if game is over
     */
    boolean isOver = false;
    /**
     * returns true if user is wins game
     */
    boolean userWon = false;
    /**
     * returns true if user successfully paired with an opponent username
     */
    boolean opponentMatched = false;

    /**
     * returns type of transaction for server
     */
    String operationInfo = "";

    /**
     * returns users unique ID
     */
    String userID = "";

    /**
     * returns true is users turn
     */
    Boolean turn = false;

    /**
     * returns opponent's unique userID
     */
    String opponent = "";

    /**
     * returns AI's message to user
     */
    String AI_Chat_Message = "";
    HashMap<String, ArrayList<Pair<Integer, Integer>>> gameBoard;
    GameInfo gameMove;

    /**
     * returns set of unique usernames currently on server
     */
    Set<String> userNames;

    /**
     * 0 if easy AI
     * 1 if medium AI
     * 2 if hard AI
     * 3 if vs. Player
     */
    int difficulty;
}
