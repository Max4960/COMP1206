package uk.ac.soton.comp1206.event;


/**
 *  The Game Loop Listener is used to keep track of when the in-game timer reaches 0
 *  It helps update the UI timer
 *
 * @author ASUS
 * @version $Id: $Id
 */
public interface GameLoopListener {

    /**
     * Handle a game loop event
     *
     * @param duration the length of the timer
     */
    public void gameLooped(int duration);
}
