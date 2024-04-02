package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import javafx.scene.control.Label;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

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

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
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
        game.start();
    }

}
