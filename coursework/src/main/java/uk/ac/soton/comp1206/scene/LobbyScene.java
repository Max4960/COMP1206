package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.Timer;

public class LobbyScene extends BaseScene {

    private Timer serverTimer;
    final Communicator communicator;
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);


    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        this.communicator = gameWindow.getCommunicator();
    }

    @Override
    public void initialise() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), event -> {
            communicator.send("LIST");
            communicator.addListener(this::fetchList);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void fetchList(String message) {
        logger.info(message);
    }

    public void fetchList() {

    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);
    }
}
