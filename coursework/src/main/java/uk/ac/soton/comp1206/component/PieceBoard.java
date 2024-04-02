package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);


    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    public void setPiece(GamePiece piece) {
        grid.playPiece(piece,1,1);
    }
}
