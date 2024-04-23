package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.util.ArrayList;
import java.util.Timer;

public class LobbyScene extends BaseScene {

    private Timer serverTimer;
    final Communicator communicator;
    private static final Logger logger = LogManager.getLogger(LobbyScene.class);
    private StackPane lobbyPane = new StackPane();
    VBox lobbyBox = new VBox();

    SimpleListProperty<String> lobbyNames;


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
        // Checking lobbies every 5 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            communicator.send("LIST");
            communicator.addListener(this::fetchList);
            Platform.runLater(() -> {
                loadLobbies();
            });
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void fetchList(String message) {
        //logger.info(message);
        String[] parts = message.split(" ");
        String data = parts[1];
        String[] lobbies = data.split("\n");
        lobbyNames = new SimpleListProperty<>();
        ArrayList<String> lobbyList = new ArrayList<String>();
        for (String lobby : lobbies) {
            lobbyList.add(lobby);
        }
        lobbyNames.set(FXCollections.observableList(lobbyList));
    }


    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(lobbyPane);

        lobbyBox.setAlignment(Pos.TOP_LEFT);
        lobbyPane.getChildren().add(lobbyBox);
        Text lobbyText = new Text("Active Lobbies:");
        lobbyText.getStyleClass().add("heading");
        lobbyBox.getChildren().add(lobbyText);
    }

    public void loadLobbies() {
        logger.info("Found (" + lobbyNames.toArray().length + ") Active Lobbies");
        lobbyBox.getChildren().clear();
        Text lobbyText = new Text("Active Lobbies:");
        lobbyText.getStyleClass().add("heading");
        lobbyBox.getChildren().add(lobbyText);
        for (String string : lobbyNames) {
            //TODO: Change this implementation
            Text name = new Text(string);
            name.getStyleClass().add("menuItem");
            lobbyBox.getChildren().add(name);
        }
    }
}
