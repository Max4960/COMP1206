// Packages
package uk.ac.soton.comp1206.game;

// Imports
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBoard;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for its display.
 */
public class Grid {

    // The number of columns in this grid
    private final int cols;

    // The number of rows in this grid
    private final int rows;

    // The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
    private final SimpleIntegerProperty[][] grid;

    // The grids Logger
    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];
        logger.info("Initialising Grid with columns: " + cols + ", and rows: " + rows);
        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Checks that the piece is able to fit in the slot selected.
     *
     * @param piece The piece being placed
     * @param x The x position of the piece
     * @param y The y position of the piece
     * @return True if the piece can be placed, False otherwise
     */
    public Boolean canPlayPiece(GamePiece piece, int x, int y) {
        int[][] shape = piece.getBlocks();
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape.length; j++) {
                if (shape[i][j] != 0) { // Skipping the empty parts of shape
                    if (get(x + i - 1,y + j - 1) != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Places a piece in the desired coordinate, does nothing if the piece can't be placed
     *
     * @param piece The piece being placed
     * @param x The x position of the piece
     * @param y The y position of the piece
     */
    public void playPiece(GamePiece piece, int x, int y) {
        logger.info("Called playPiece");
        if (canPlayPiece(piece, x, y)) {
            int[][] shape = piece.getBlocks();  // Fetching the shape we want
            for (int i = 0; i < shape.length; i++) {    // This is between 1-3
                for (int j = 0; j < shape.length; j++) {// Go to row, then check all columns
                    // Check the value in the matrix is not 0 -> block should be present
                    if (shape[i][j] != 0) {
                        set(x + i - 1, y + j - 1, piece.getValue()); // Needs -1 as offset
                    }
                }
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

}
