package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);
    //private BlockClickedListener blockClickedListener;

    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    public void setPiece(GamePiece piece) {
        // Need to clean board first
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                grid.set(x,y,0);
            }
        }
        grid.playPiece(piece,1,1);
    }

}
