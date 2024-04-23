package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
    private boolean inLobby = false;
    VBox lobbyBox = new VBox();
    VBox chatBox = new VBox();

    SimpleListProperty<String> lobbyNames;
    ListView<String> lobbyListViewer = new ListView<>();


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
            if (!inLobby) {
                communicator.send("LIST");
                //communicator.addListener(this::fetchList);
                //Platform.runLater(() -> {
                //    loadLobbies();
                //});
            }

        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        communicator.addListener(event -> {
            Platform.runLater(() -> {
                receiver(event);
            });
        });

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

    private void receiver(String message) {
        logger.info("Receiver received " + message);
        String[] parts = message.split(" ");
        String command = parts[0];
        String info = parts[1];

        logger.info(command);
        logger.info(info);

        switch (command) {
            case "JOIN":
                if (!inLobby) {
                    inLobby = true;
                    joinLobby(info);
                }
                break;
            case "HOST":
                break;
            case "CHANNELS":
                fetchList(message);
                loadLobbies();
                logger.info(command + " " + info);
                break;
            case "NICK":
                break;
            case "START":
                break;
            case "PARTED":
                break;
            case "USERS":
                break;
            case "MSG":
                break;
            case "ERROR":
                break;
            case "QUIT":
                break;
        }
    }


    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(lobbyPane);

        //chatBox.setAlignment(Pos.TOP_RIGHT);
        //chatBox.setMaxWidth(0.4*gameWindow.getWidth());
        //chatBox.setStyle("-fx-background-color: black;");

        BorderPane gameInfoBox = new BorderPane();
        gameInfoBox.setMaxWidth(0.4*gameWindow.getWidth());
        gameInfoBox.setAlignment(root, Pos.CENTER_RIGHT);
        gameInfoBox.getStyleClass().add("gameBox");
        lobbyPane.getChildren().add(gameInfoBox);

        lobbyBox.setAlignment(Pos.TOP_LEFT);
        lobbyPane.getChildren().add(lobbyBox);
        Text lobbyText = new Text("Active Lobbies:");
        lobbyText.getStyleClass().add("heading");
        lobbyBox.getChildren().add(lobbyText);
        lobbyListViewer.setMaxWidth(0.4*gameWindow.getWidth());
        // *Graphics design is my passion*
        lobbyListViewer.setStyle("-fx-background-color: black; -fx-opacity: 0.8; -fx-border-color: red;");
        lobbyBox.getChildren().add(lobbyListViewer);

        TextField createLobbyName = new TextField();
        createLobbyName.setMaxWidth(0.4*gameWindow.getWidth());
        createLobbyName.setPromptText("Enter a lobby name...");
        lobbyBox.getChildren().addAll(createLobbyName);

        Button createLobbyButton = new Button("Create Lobby");
        createLobbyButton.setStyle("-fx-background-color: red; -fx-text-fill: white");
        lobbyBox.getChildren().add(createLobbyButton);

        createLobbyButton.setOnAction(event -> {
            createLobby(createLobbyName.getText());
        });

        lobbyListViewer.setOnMouseClicked(event -> {
            String selectedLobby = lobbyListViewer.getSelectionModel().getSelectedItem();
            //joinLobby(selectedLobby);
            communicator.send("JOIN " + selectedLobby);
        });
    }

    public void createLobby(String lobbyName) {
        communicator.send("CREATE " + lobbyName);
    }

    public void joinLobby(String lobby) {
        inLobby = true;
        logger.info("Joining: " + lobby);

    }

    public void loadLobbies() {
        logger.info("Found (" + lobbyNames.toArray().length + ") Active Lobbies");
        lobbyListViewer.setItems(lobbyNames);

    }
}
