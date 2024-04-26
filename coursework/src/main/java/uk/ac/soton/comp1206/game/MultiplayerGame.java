package uk.ac.soton.comp1206.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.util.*;

/**
 * Multiplayer Game extends the game class
 * Has methods to handle the unique functionality of a multiplayer game
 */
public class MultiplayerGame extends Game {

    /**
     * The MultiplayerGame's communicator
     */
    Communicator communicator;
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    /**
     * A queue representing the next pieces to be played
     */
    private Queue<GamePiece> pieces = new LinkedList<>();

    /**
     * ArrayList representing the players scores
     */
    private ArrayList<Pair<String, Integer>> playerScores;

    private int pieceTracker = 0;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     * @param communicator a object
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator) {
        super(cols, rows);
        this.communicator = communicator;
    }

    /**
     * The reciever methods recieves messages from the communicator and handles them accordingly
     */
    private void receiver(String message) {
        logger.info("Receiver received " + message);
        String[] parts = message.split(" ");
        String[] data = message.split(" ", 2);

        String command = parts[0];
        String info = "";
        if (parts.length > 1) {
            info = parts[1];
        }

        switch (command) {
            case "PIECE":
                if (pieceTracker == 0) {
                    spawnPiece(Integer.parseInt(info));
                    currentPiece = pieces.remove();
                } else if (pieceTracker == 1) {
                    spawnPiece(Integer.parseInt(info));
                    followingPiece = pieces.remove();
                    nextPieceListener.nextPiece(currentPiece, followingPiece);
                } else {
                    spawnPiece(Integer.parseInt(info));
                }
                pieceTracker++;
            case "SCORE":
                break;
            case "SCORES":
                //scoresHandler(info);
                break;
            case "MSG":
                break;
            case "BOARD":
                break;
            default:
                break;
        }
    }



    /**
     * Sorts the score list
     */
    public void sortScores() {  // Adapted from ScoreScene
        // Sorting by integer values
        Collections.sort(playerScores, new Comparator<Pair<String,Integer>>() {
            // Sorts Highest -> Lowest
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                if (o1.getValue() > o2.getValue()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }



    /**
     * Spawns a game piece
     *
     * @param value value of the piece to be spawned
     */
    public void spawnPiece(int value) {
        GamePiece piece = GamePiece.createPiece(value);
        pieces.add(piece);
        logger.info(pieces);
    }




    /**
     * Handles setting the pieces
     */
    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = pieces.remove();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        communicator.send("PIECE");
    }

    /**
     * Called when bar reaches 0
     * Decreases life, generates a piece, resets bar
     */
    public void gameLoop() {
        int currentLife = lives.get();
        if (currentLife > 0) {
            currentLife--;
            Multimedia.playAudio("lifelose.wav");
            lives.set(currentLife);
            this.nextPiece();
            communicator.send("LIVES " + currentLife);
            loop();
            manageTimer();
            logger.info("New Game Loop Started");
        } else {
            Multimedia.playAudio("explode.wav");
            logger.info("Game Over");
            communicator.send("DIE");
            showScoreListener.gameOver();
        }
    }

    /**
     * Handles when a block is clicked
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
            this.nextPiece();
        } else {
            Multimedia.playAudio("fail.wav");
        }
    }


    /**
     * Initialises the game
     */
    @Override
    public void initialiseGame() {
        communicator.addListener(event -> {
            Platform.runLater(() -> {
                receiver(event);
            });
        });
        playerScores = new ArrayList<>();
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
        communicator.send("PIECE");
    }
}
