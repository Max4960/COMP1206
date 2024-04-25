package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);


    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        // Logo


        //For now, let us just add a button that starts the game. I'm sure you'll do something way better.
        //var button = new Button("Play");
        //mainPane.setBottom(button);

        //Bind the button action to the startGame method in the menu
        //button.setOnAction(this::startGame);
        VBox menu = new VBox();
        menu.setAlignment(Pos.CENTER);
        menu.setPadding(new Insets(100));
        mainPane.setTop(menu);



        // Logo
        Image logoBackdrop = new Image(String.valueOf(Multimedia.class.getResource("/images/TetrECS.png")));
        ImageView logoBackdropView = new ImageView(logoBackdrop);
        logoBackdropView.setFitWidth(gameWindow.getWidth()/2);
        logoBackdropView.setFitHeight(gameWindow.getHeight()/2);
        logoBackdropView.setPreserveRatio(true);
        menu.getChildren().add(logoBackdropView);

        AnimationTimer timer = new AnimationTimer() {
            double scale = 1.0;
            double factor = 0.01;
            double rotation = 0.0;
            double speed = 0.1;
            @Override
            public void handle(long l) {
                scale += factor;
                logoBackdropView.setScaleX(scale);
                logoBackdropView.setScaleY(scale);
                if (scale > 2 || scale < 1) {
                    factor *= -1;
                    speed *= -1;
                }
                rotation += speed;
                logoBackdropView.setRotate(rotation);
            }
        };
        timer.start();

        Text start = new Text("Start Game");
        start.getStyleClass().add("menuItem");
        menu.getChildren().add(start);

        Text multi = new Text("Multiplayer");
        multi.getStyleClass().add("menuItem");
        menu.getChildren().add(multi);

        Text info = new Text("Information");
        info.getStyleClass().add("menuItem");
        menu.getChildren().add(info);

        Text quit = new Text("Quit Game");
        quit.getStyleClass().add("menuItem");
        menu.getChildren().add(quit);

        start.setOnMouseClicked(event -> {
            gameWindow.startChallenge();
        });

        multi.setOnMouseClicked(event -> {
            gameWindow.startLobby();
        });

        info.setOnMouseClicked(event -> {
            gameWindow.startInformation();
        });

        quit.setOnMouseClicked(event -> {
            gameWindow.quit();
        });
    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        Multimedia.playMusic("menu.mp3");
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

}
