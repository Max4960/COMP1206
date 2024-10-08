// Packages
package uk.ac.soton.comp1206.game;

// Imports
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.ShowScoreListener;
import uk.ac.soton.comp1206.scene.ChallengeScene;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

    /**
     * The next piece listener
     */
    public NextPieceListener nextPieceListener;

    /**
     * The line cleared listener
     */
    public LineClearedListener lineClearedListener;

    /**
     * The game loop listener
     */
    private GameLoopListener gameLoopListener;

    /**
     * The show score listener
     */
    public ShowScoreListener showScoreListener;

    /**
     * The Game piece currently being played
     */
    public GamePiece currentPiece; // NTS: Needs to be public to display first piece generated on piece board
    /**
     * The next piece to be placed
     */
    public GamePiece followingPiece;
    /**
     * The initial score value stored as a Simple Integer Property to allow binding
     */
    public SimpleIntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * The initial value for level stored as a Simple Integer Property (note: in this game we start at level zero)
     */
    public SimpleIntegerProperty level = new SimpleIntegerProperty(0); // Level begins at 0
    /**
     * The initial value for lives stored as a Simple Integer Property
     */
    public SimpleIntegerProperty lives = new SimpleIntegerProperty(3);

    /**
     * Member variable representing the multiplier, its default value is one
     */
    private int multiplier = 1;

    /**
     * The timer used for the game loop
     */
    public Timer timer;

    /**
     * The timer task associated with the game loop
     */
    public TimerTask loop;


    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        currentPiece = spawnPiece();
        followingPiece = spawnPiece();


        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Starts the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        manageTimer();
    }

    /**
     * Updates the timer
     */
    public void manageTimer() {
        loop = new TimerTask() {
            public void run() {
                gameLoop();
            }
        };
        timer = new Timer();
        timer.schedule(loop, getTimerDelay());
        loop();
    }

    /**
     * Called when the bar reaches 0
     * Decreases life. generates a new piece, and resets the bar
     */
    public void gameLoop() {
        int currentLife = lives.get();
        if (currentLife > 0) {
            currentLife--;
            Multimedia.playAudio("lifelose.wav");
            lives.set(currentLife);
            nextPiece();
            loop();
            manageTimer();
            logger.info("New Game Loop Started");
        } else {
            Multimedia.playAudio("explode.wav");
            logger.info("Game Over");
            showScoreListener.gameOver();
        }
    }

    /**
     * A support method that loops the timer
     */
    public void loop() {
        if (this.gameLoopListener != null) {
            this.gameLoopListener.gameLooped(getTimerDelay());
            logger.info("Timer Delay - " + getTimerDelay() + " milliseconds");
        }
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handles what should happen when a particular block is clicked
     *
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        // Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        // Check that a piece can be placed
        if (getGrid().canPlayPiece(currentPiece, x, y)) {
            Multimedia.playAudio("boom.mp3");
            // Piece placement
            grid.playPiece(currentPiece, x, y);
            loop.cancel();
            manageTimer();
            afterPiece();
            nextPiece();
        } else {
            Multimedia.playAudio("fail.wav");
        }
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     *
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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
     * Gets the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Gets a random Game Piece
     * @return random Game Piece
     */
    private GamePiece spawnPiece() {
        Random toPlace = new Random();
        return GamePiece.createPiece(toPlace.nextInt(GamePiece.PIECES));
    }

    /**
     * Updates currentPiece to a new piece
     */
    private void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    /**
     * Handles the game logic after a move is made
     */
    public void afterPiece() {
        // Local variables keeping track of lines and blocks being cleared
        int lines = 0;
        int blocks = 0;
        HashSet<GameBlockCoordinate> toClear = new HashSet<GameBlockCoordinate>();  // IntegerProperty -> (x,y)
        // Looping through the rows - This for loop clears vertical lines
        for (int i = 0; i < rows; i++) {
            int count = 0;
            for (int j = 0; j < cols; j++) {
                if (grid.get(i,j) != 0) {
                    count++;
                } else {    // No point checking again if there is a 0, improves efficiency
                    break;
                }
            }
            // It is a full row so need to add all coordinates to the HashSet
            if (count == rows) {
                Multimedia.playAudio("clear.wav");
                for (int j = 0; j < cols; j++) {
                    GameBlockCoordinate coordinate = new GameBlockCoordinate(i,j);
                    toClear.add(coordinate);
                    lineClearedListener.lineCleared(toClear);
                }
                lines++;
            }
        }
        // Clears horizontal lines
        for (int j = 0; j < cols; j++) {
            int count = 0;
            for (int i = 0; i < rows; i++) {
                if (grid.get(i,j) != 0) {
                    count++;
                } else {    // No point checking again if there is a 0
                    break;
                }
            }
            // It is a full column so need to add all coordinates to the HashSet
            if (count == cols) {
                Multimedia.playAudio("clear.wav");
                for (int i = 0; i < cols; i++) {
                    GameBlockCoordinate coordinate = new GameBlockCoordinate(i,j);
                    toClear.add(coordinate);
                    lineClearedListener.lineCleared(toClear);
                }
                lines++;
            }
        }

        logger.info("Clearing: "+ toClear);
        // Loop through the hashset and remove all blocks
        for (GameBlockCoordinate coordinate : toClear) {
            grid.set(coordinate.getX(), coordinate.getY(), 0);
            blocks++;
        }
        // Updating the score
        int current = score.get();
        score.set(current + score(lines,blocks));
        updateMultiplier(toClear);
        levelUp();
    }

    /**
     * Returns what the score should be
     * @param noLines The number of lines cleared
     * @param noBlocks The number of unique blocks cleared
     * @return The score achieved this move
     */
    public int score(int noLines, int noBlocks) {
        return (noLines * noBlocks * 10 * multiplier);
    }

    /**
     * Updates the multiplier if blocks have been cleared
     * Otherwise resets the multiplier to 1
     * @param set The set of blocks to be cleared
     */
    public void updateMultiplier(HashSet set) {
        if (!set.isEmpty()) {
            multiplier++;
        } else {
            multiplier = 1;
        }
    }

    /**
     * Increases the level per 1000 points
     */
    public void levelUp() {
        int currentScore = score.get();
        level.set(currentScore / 1000);
    }

    /**
     * Sets the Next Piece Listener
     *
     * @param listener representing the next piece listener
     */
    public void setNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * Sets the Line Cleared Listener
     *
     * @param listener representing the line cleared listener
     */
    public void setLineClearedListener(LineClearedListener listener) {
        this.lineClearedListener = listener;
    }

    /**
     * Sets the Game Loop Listener
     *
     * @param listener representing the game loop listener
     */
    public void setGameLoopListener(GameLoopListener listener) {
        this.gameLoopListener = listener;
    }

    /**
     * Sets the Show Score Listener
     *
     * @param listener representing the show score listener
     */
    public void setShowScoreListener(ShowScoreListener listener) {
        this.showScoreListener = listener;
    }

    /**
     * Getter to fetch the game score
     * @return return int of the game score
     */
    public int getScore() {
        return score.get();
    }

    /**
     * Rotates the piece based on the Right-Clicked Listener
     *
     * @param piece a Game Piece object
     * @param rotations a int stating the rotations
     */
    public void rotateCurrentPiece(GamePiece piece, int rotations) {
        //Multimedia.playAudio("rotate.wav");
        piece.rotate(rotations);
        logger.info("Rotating " + piece + " with rotations: " + rotations);
    }

    /**
     * Swaps the pieces around
     */
    public void swapCurrentPiece() {
        Multimedia.playAudio("transition.wav");
        GamePiece temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
        logger.info("Swapping " + currentPiece + " to " + followingPiece);
    }

    /**
     * Calculates the value for the timer delay
     *
     * @return an int value of the timer delay in milliseconds
     */
    public int getTimerDelay() {
        int delay = 12000 - (500 * level.get());
        if (delay <= 2500) {
            delay = 2500;
        }
        return delay;
    }

    /**
     * Stops the game timer
     */
    public void killTimer() {
        timer.cancel();
        timer.purge();
        loop.cancel();
        logger.info("Killing timer");
    }

}
