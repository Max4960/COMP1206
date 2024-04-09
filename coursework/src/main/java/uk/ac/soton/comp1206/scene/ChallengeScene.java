// Packages
package uk.ac.soton.comp1206.scene;

// Imports
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import javafx.scene.control.Label;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * The instance of the Game being used
     */
    protected Game game;

    /**
     * Piece board representing the current piece being placed
     */
    protected PieceBoard current;

    /**
     * Piece board representing the next piece to place
     */
    protected PieceBoard follower;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // Score User Interface
        VBox scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        Text scoreText = new Text("SCORE");
        Text scoreValue = new Text();
        scoreValue.textProperty().bind(game.score.asString());
        scoreText.getStyleClass().add("heading");
        scoreValue.getStyleClass().add("score");
        scoreBox.getChildren().add(scoreText);
        scoreBox.getChildren().add(scoreValue);
        mainPane.setLeft(scoreBox);

        // Level User Interface
        VBox levelBox = new VBox();
        levelBox.setAlignment(Pos.CENTER);
        Text levelText = new Text("LEVEL");
        Text levelValue = new Text();
        levelValue.textProperty().bind(game.level.asString());
        levelText.getStyleClass().add("heading");
        levelValue.getStyleClass().add("level");
        levelBox.getChildren().add(levelText);
        levelBox.getChildren().add(levelValue);
        mainPane.setRight(levelBox);

        // Lives User Interface
        HBox livesBox = new HBox();
        livesBox.setAlignment(Pos.CENTER);
        Text livesText = new Text("LIVES : ");
        Text livesValue = new Text();
        livesValue.textProperty().bind(game.lives.asString());
        livesText.getStyleClass().add("heading");
        livesValue.getStyleClass().add("lives");
        livesBox.getChildren().add(livesText);
        livesBox.getChildren().add(livesValue);
        mainPane.setTop(livesBox);

        // Setting up board and the two sub boards
        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);
        current = new PieceBoard(3,3,gameWindow.getWidth()/4, gameWindow.getWidth()/4);
        levelBox.getChildren().add(current);
        current.setTranslateY(50);
        current.setTranslateX(-10);
        follower = new PieceBoard(3, 3, gameWindow.getWidth()/5, gameWindow.getWidth()/5);
        levelBox.getChildren().add(follower);
        follower.setTranslateY(60);

        //Handle block on game board grid being clicked
        board.setOnBlockClick(this::blockClicked);
        // For rotations
        board.setOnRightClicked(this::rotate);
        current.setOnMouseClicked(event -> {
            rotate();
        });

        // Used to update next pieces
        game.setNextPieceListener((first, second) -> {
            //current.setPiece(piece);
            nextPiece(first, second);
            logger.info("CALLED" + first.toString());
        });
    }


    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Called when the board is right-clicked or piece board is clicked
     * Rotates the block and updates the current piece board
     */
    private void rotate() { // Formerly called rightClick()
        // Rotates the piece
        game.rotateCurrentPiece(game.currentPiece);
        logger.info("Piece Rotated");
        // Updates the piece board with the rotation
        current.setPiece(game.currentPiece);
    }

    /**
     * Used to set the next pieces, current & follower are piece boards
     * @param first - The piece currently being placed
     * @param second - The next piece to be placed
     */
    private void nextPiece(GamePiece first, GamePiece second) {
        current.setPiece(first);
        follower.setPiece(second);
    }

    /**
     * Set up the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);

    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        Multimedia.playMusic("game.wav");
        // This is vital for displaying the first piece(s)
        current.setPiece(game.currentPiece);
        follower.setPiece(game.followingPiece);
        game.start();

        // Checks if ESC has been pressed
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    gameWindow.startMenu();
                default:
                    break;
            }
        });
    }



}
