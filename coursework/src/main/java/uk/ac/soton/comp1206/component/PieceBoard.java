package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * Is an extension of the gameboard class
 * Handles the visuals of the current and next piece
 */
public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    //private BlockClickedListener blockClickedListener;

    /**
     * The constructor for pieceboard
     *
     * @param cols an int showing columns
     * @param rows an int showing rows
     * @param width a double
     * @param height a double
     */
    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    /**
     * Clears the board then sets the board to have the desired piece
     *
     * @param piece a piece object
     */
    public void setPiece(GamePiece piece) {
        // Need to clean board first
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                grid.set(x,y,0);
                if (x == 1 && y == 1) {
                    getBlock(x,y).setCentre(true);
                }
            }
        }
        grid.playPiece(piece,1,1);
    }
}
