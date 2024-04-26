package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.Set;

/**
 * The Line Cleared Listener is called when a line is cleared in-game
 * It takes a set of block coordinates to have the fade out animation played
 *
 * @author ASUS
 * @version $Id: $Id
 */
public interface LineClearedListener {

    /**
     * Handle a line cleared event
     *
     * @param blockCoordinateSet the set of Game Block Coordinates that have been cleared
     */
    public void lineCleared(Set<GameBlockCoordinate> blockCoordinateSet);
}
