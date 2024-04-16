import javafx.util.Pair;

public interface Engine {
    /*
        FUNCTIONAL INTERFACE
        --------------------
        Each Game Engine Algorithm that implements the Engine Interface
        must define the make move function in order for the AI Algorithm
        to make a move for the game versus another player.
     */

    /**
     * Makes a move on behalf of the computer that the player is playing
     * against based on the feedback of the algorithm's previous move.
     *
     * @param info information about the results of the previous move
     * @return Pair&lt;Integer, Integer&gt; representing (x, y) coordinate
     *         for desired move
     */
    public Pair<Integer, Integer> makeMove(GameInfo info);
}
