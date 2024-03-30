package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

import java.util.HashSet;
import java.util.Random;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    // !---------- Added by Max ----------!
    public GamePiece currentPiece;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        currentPiece = spawnPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        //int previousValue = grid.get(x,y);
        //int newValue = previousValue + 1;
        //if (newValue  > GamePiece.PIECES) {
        //    newValue = 0;
        //}
        //GamePiece.createPiece(0);
        grid.playPiece(currentPiece, x, y);
        afterPiece();
        nextPiece();

        //Update the grid with the new value
        //grid.set(x,y,newValue);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    public GamePiece spawnPiece() {
        Random toPlace = new Random();
        return GamePiece.createPiece(toPlace.nextInt(GamePiece.PIECES));
    }

    public void nextPiece() {
        currentPiece = spawnPiece();
    }

    public void afterPiece() {
        HashSet<IntegerProperty> toClear = new HashSet<IntegerProperty>();  // IntegerProperty -> (x,y)
        // Looping through the rows
        for (int i = 0; i < cols; i++) {
            int count = 0;
            for (int j = 0; j < rows; j++) {
                if (grid.get(i,j) != 0) {
                    count++;
                } else {    // No point checking again if there is a 0
                    break;
                }
            }
            if (count == rows) {
                for (int j = 0; j < rows; j++) {
                    IntegerProperty coordinate = grid.getGridProperty(i,j);
                    toClear.add(coordinate);
                }
            }
        }


    }

}
