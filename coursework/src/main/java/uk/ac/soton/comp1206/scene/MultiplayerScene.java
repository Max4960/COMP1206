package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

public class MultiplayerScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MultiplayerScene.class);

    /**
     * The instance of the Game being used
     */
    protected Game game;

    protected GameBoard gameBoard;

    /**
     * Piece board representing the current piece being placed
     */
    protected PieceBoard current;

    /**
     * Piece board representing the next piece to place
     */
    protected PieceBoard follower;
    private Rectangle timeRect;
    private double startWidth;

    private int xLocation = 2;  // Used for keys
    private int yLocation = 2;  // Want to start in the center to improve game feel

    private IntegerProperty highestScore = new SimpleIntegerProperty();

    private Communicator communicator;

    /**
     * Create a new Multi Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        communicator = gameWindow.getCommunicator();
    }

    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        checkHighScore();
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            checkHighScore();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // Score User Interface
        VBox scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        javafx.scene.text.Text highScoreText = new javafx.scene.text.Text("HIGHSCORE");
        javafx.scene.text.Text highScoreValue = new javafx.scene.text.Text();
        highScoreValue.textProperty().bind(highestScore.asString());
        highScoreText.getStyleClass().add("heading");
        highScoreValue.getStyleClass().add("score");
        scoreBox.getChildren().add(highScoreText);
        scoreBox.getChildren().add(highScoreValue);

        javafx.scene.text.Text scoreText = new javafx.scene.text.Text("SCORE");
        javafx.scene.text.Text scoreValue = new javafx.scene.text.Text();
        scoreValue.textProperty().bind(game.score.asString());
        scoreText.getStyleClass().add("heading");
        scoreValue.getStyleClass().add("score");
        scoreBox.getChildren().add(scoreText);
        scoreBox.getChildren().add(scoreValue);
        mainPane.setLeft(scoreBox);

        // Level User Interface
        VBox levelBox = new VBox();
        levelBox.setAlignment(Pos.CENTER);
        javafx.scene.text.Text levelText = new javafx.scene.text.Text("LEVEL");
        javafx.scene.text.Text levelValue = new javafx.scene.text.Text();
        levelValue.textProperty().bind(game.level.asString());
        levelText.getStyleClass().add("heading");
        levelValue.getStyleClass().add("level");
        levelBox.getChildren().add(levelText);
        levelBox.getChildren().add(levelValue);
        mainPane.setRight(levelBox);

        // Lives User Interface
        HBox livesBox = new HBox();
        livesBox.setAlignment(Pos.CENTER);
        javafx.scene.text.Text livesText = new javafx.scene.text.Text("LIVES : ");
        javafx.scene.text.Text livesValue = new Text();
        livesValue.textProperty().bind(game.lives.asString());
        livesText.getStyleClass().add("heading");
        livesValue.getStyleClass().add("lives");
        livesBox.getChildren().add(livesText);
        livesBox.getChildren().add(livesValue);
        mainPane.setTop(livesBox);

        // Setting up board and the two sub boards
        gameBoard = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(gameBoard);
        current = new PieceBoard(3,3,gameWindow.getWidth()/4, gameWindow.getWidth()/4);
        levelBox.getChildren().add(current);
        current.setTranslateY(50);
        current.setTranslateX(-10);
        follower = new PieceBoard(3, 3, gameWindow.getWidth()/5, gameWindow.getWidth()/5);
        levelBox.getChildren().add(follower);
        follower.setTranslateY(60);

        // Timer - Green bar at bottom of screen
        timeRect = new Rectangle();
        timeRect.setHeight(gameWindow.getHeight()/11);
        timeRect.setWidth(gameWindow.getWidth()/1.3);
        timeRect.setFill(Color.LIME);
        startWidth = timeRect.getWidth();   // Has to be declared outside the countDown function
        mainPane.setBottom(timeRect);

        //Handle block on game board grid being clicked
        gameBoard.setOnBlockClick(this::blockClicked);
        // For rotations
        gameBoard.setOnRightClicked(this::rotateRight);
        current.setOnMouseClicked(event -> {
            rotateRight();
        });

        // Used to update next pieces
        game.setNextPieceListener((first, second) -> {
            //current.setPiece(piece);
            nextPiece(first, second);
            logger.info("New Piece " + first.toString());
        });

        game.setGameLoopListener((event) -> {
            countDown(game.getTimerDelay());
        });
        game.setLineClearedListener(this::lineCleared);
    }

    private void inputHandler(KeyEvent key) {
        switch (key.getCode()) {
            case ESCAPE:
                gameWindow.startMenu();
                game.killTimer();
                break;
            case UP: // This formatting acts like an OR
            case W:
                logger.info("Position: (" + xLocation + ", "+ yLocation + ")");
                if (yLocation > 0) {
                    yLocation--;
                }
                break;
            case DOWN:
            case S:
                logger.info("Position: (" + xLocation + ", "+ yLocation + ")");
                if (yLocation < gameBoard.getRowCount() - 1) {
                    yLocation++;
                }
                break;
            case LEFT:
            case A:
                logger.info("Position: (" + xLocation + ", "+ yLocation + ")");
                if (xLocation > 0) {
                    xLocation--;
                }
                break;
            case RIGHT:
            case D:
                logger.info("Position: (" + xLocation + ", "+ yLocation + ")");
                if (xLocation < gameBoard.getColumnCount() - 1) {
                    xLocation++;
                }
                break;
            case ENTER:
            case X:
                game.blockClicked(gameBoard.getBlock(xLocation,yLocation));
                logger.info("Position: (" + xLocation + ", "+ yLocation + ")");
                xLocation = 2;  //Recentering
                yLocation = 2;
                break;
            case OPEN_BRACKET:
            case Q:
            case Z:
                //ROTATE LEFT
                rotateLeft();
                break;
            case CLOSE_BRACKET:
            case E:
            case C:
                //ROTATE RIGHT
                rotateRight();
                break;
            case SPACE:
            case R:
                //SWAP
                game.swapCurrentPiece();
                current.setPiece(game.currentPiece);
                follower.setPiece(game.followingPiece);
                break;
            case B:
                game.lives.set(0);
                break;

            default:
                break;
        }
        for(var y = 0; y < game.getRows(); y++) {   // There is probably a better way to do this
            for(var x = 0; x < game.getCols(); x++) {
                if (y == yLocation && x == xLocation) {
                    //gameBoard.getBlock(x,y).highlight();
                    if (gameBoard.getBlock(x,y).getValue() == 0) {
                        gameBoard.getBlock(x,y).highlight(true);
                    } else {
                        gameBoard.getBlock(x,y).highlight(false);
                    }
                } else {
                    gameBoard.getBlock(x,y).paint();
                }
            }
        }
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
    private void rotateRight() { // Formerly called rightClick()
        // Rotates the piece
        game.rotateCurrentPiece(game.currentPiece, 1);
        logger.info("Piece Rotated");
        // Updates the piece board with the rotation
        current.setPiece(game.currentPiece);
    }

    private void rotateLeft() {
        game.rotateCurrentPiece(game.currentPiece, 3);
        logger.info("Rotated Left");
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

    private void lineCleared(Set<GameBlockCoordinate> blockCoordinateSet) {
        for (GameBlockCoordinate gameBlockCoordinate : blockCoordinateSet) {
            gameBoard.getBlock(gameBlockCoordinate.getX(),gameBlockCoordinate.getY()).fadeOut();
        }
    }

    private void countDown(int time) {
        long startTime = System.nanoTime(); // Have to use nanoTime
        AnimationTimer animationTimer = new AnimationTimer() {
            long duration = time;
            double initialRed = 0;
            double initialGreen = 1;
            @Override
            public void handle(long processTime) {
                long elapsed = (processTime - startTime)/1000000;
                long remaining = duration - elapsed;
                double ratio = (double)(remaining)/(double)(duration);
                if (remaining > 0) {
                    timeRect.setWidth(startWidth*(ratio));
                    double red = initialRed + (1-ratio);
                    double green = initialGreen * ratio;
                    timeRect.setFill(Color.color(red, green, 0));
                } else {
                    stop();
                }

            }
        };
        animationTimer.start();
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
        game.setGameLoopListener(this::countDown);
        game.start();
        game.setShowScoreListener(this::loadScores);
        scene.setOnKeyReleased(this::inputHandler);
    }

    private void loadScores() {
        game.timer.purge(); // Cleaning threads as a javafx function cant be called on a timer thread
        game.timer.cancel();
        Platform.runLater(()->gameWindow.startScore());

    }

    public Game getGame() {
        return game;
    }

    public void checkHighScore() {
        try {
            getHighScore();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getHighScore() throws IOException {
        int prevHighScore;
        String fileName = "scores.txt";
        File file = new File(fileName);
        if (file.exists() && file.length() != 0) {
            FileReader fr = new FileReader("scores.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            line = br.readLine();
            String[] parts = line.split(":");
            prevHighScore = Integer.parseInt(parts[1]);
        } else {
            prevHighScore = 0;
        }
        if (prevHighScore >= game.score.get()) {
            highestScore.set(prevHighScore);
        } else {
            highestScore.set(game.score.get());
        }
    }
}
