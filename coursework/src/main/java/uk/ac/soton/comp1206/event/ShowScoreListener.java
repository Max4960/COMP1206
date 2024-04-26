package uk.ac.soton.comp1206.event;

/**
 * Used to display the scores once the game is over
 */
public interface ShowScoreListener {

    /**
     * Handle a game over event
     */
    public void gameOver();
}
