package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * The Next Piece Listener is called whenever a new piece is generated
 * This is used to update the representing piece boards
 *
 * @author ASUS
 * @version $Id: $Id
 */
public interface NextPieceListener {

    /**
     * Handle a next piece event
     *
     * @param firstPiece the current piece being placed
     * @param secondPiece the next piece to be placed
     */
    public void nextPiece(GamePiece firstPiece, GamePiece secondPiece);
}
