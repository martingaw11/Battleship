import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

public class GameMessage implements Serializable {
    static final long serialVersionUID = 42L;
    /**
     * returns true if user is new to server
     */
    boolean newUser = false;
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
     * returns message from user
     */
    String MessageInfo = "";

    /**
     * returns type of transaction for server
     */
    String operationInfo = "";

    /**
     * returns users unique ID
     */
    String userID = "";

    /**
     * returns opponent's unique userID
     */
    String opponent = "";

    /**
     * returns AI's message to user
     */
    String AI_Chat_Message = "";
    ArrayList<ArrayList<Integer>> gameBoard;
    GameInfo gameMove;

    /**
     * returns set of unique usernames currently on server
     */
    Set<String> userNames;

    /**
     * returns recipient of message
     */
    String recipient;  // may keep this for mega chat hub
}
