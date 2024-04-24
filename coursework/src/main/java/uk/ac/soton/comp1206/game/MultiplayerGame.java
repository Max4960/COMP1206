package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;

import java.util.ArrayList;
import java.util.Queue;

public class MultiplayerGame extends Game {

    Communicator communicator;
    private static final Logger logger = LogManager.getLogger(Game.class);
    private ArrayList<GamePiece> pieces = new ArrayList<GamePiece>();
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
                pieceHandler(Integer.parseInt(info));
            case "SCORE":
                break;
            case "SCORES":
                break;
            case "MSG":
                break;
            case "BOARD":
                break;
            default:
                break;
        }
    }

    private void pieceHandler(int value) {
        GamePiece piece = GamePiece.createPiece(value);
        pieces.add(piece);

        if (pieceTracker == 0) {
            spawnPiece();
            nextPiece();
            pieceTracker++;
        } else if (pieceTracker <= 3) {
            communicator.send("PIECE");
            followingPiece = pieces.get(0);
            pieces.remove(0);
            nextPiece();
            pieceTracker++;
        }
    }

    private void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        nextPieceListener.nextPiece(currentPiece, followingPiece);
    }

    private GamePiece spawnPiece() {
        communicator.send("PIECE");
        GamePiece first = pieces.get(0);
        pieces.remove(0);
        return first;
    }

    @Override
    public void initialiseGame() {
        communicator.addListener(event -> {
            Platform.runLater(() -> {
                receiver(event);
            });
        });

        for (int i = 0; i < 3; i++) {   // Generating first few pieces
            communicator.send("PIECE");
        }
    }
}
