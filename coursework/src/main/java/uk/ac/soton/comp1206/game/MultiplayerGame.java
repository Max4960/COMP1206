package uk.ac.soton.comp1206.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.network.Communicator;

import java.util.*;

public class MultiplayerGame extends Game {

    Communicator communicator;
    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);
    private Queue<GamePiece> pieces = new LinkedList<>();

    private ArrayList<Pair<String, Integer>> playerScores;

    int pieceTracker = 0;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator) {
        super(cols, rows);
        this.communicator = communicator;
    }


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

    public void scoresHandler(String data) {
        playerScores.clear();
        String[] lines = data.split("\n");

        for (String line : lines) {
            String[] parts = line.split(":");
            playerScores.add(new Pair(parts[0], Integer.parseInt(parts[1])));
        }
        sortScores();
    }

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

    public void spawnPiece(int value) {
        GamePiece piece = GamePiece.createPiece(value);
        pieces.add(piece);
        logger.info(pieces);
    }

    public ArrayList<Pair<String, Integer>> getPlayerScores() {
        return playerScores;
    }


    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = pieces.remove();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
        communicator.send("PIECE");
    }

    public void gameLoop() {
        int currentLife = lives.get();
        if (currentLife > 0) {
            currentLife--;
            lives.set(currentLife);
            this.nextPiece();
            loop();
            manageTimer();
            logger.info("New Game Loop Started");
        } else {
            logger.info("Game Over");
            showScoreListener.gameOver();
        }
    }

    public void blockClicked(GameBlock gameBlock) {
        // Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();
        // Check that a piece can be placed
        if (getGrid().canPlayPiece(currentPiece, x, y)) {
            // Piece placement
            grid.playPiece(currentPiece, x, y);
            loop.cancel();
            manageTimer();
            afterPiece();
            this.nextPiece();
        }
    }


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
