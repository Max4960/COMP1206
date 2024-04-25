package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

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
    Button startGameButton = new Button("Start");


    SimpleListProperty<String> lobbyNames = new SimpleListProperty<String>(FXCollections.observableArrayList());
    ListView<String> lobbyListViewer = new ListView<>();
    BorderPane gameInfoBox = new BorderPane();
    TextFlow textflow = new TextFlow();



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
        Multimedia.playMusic("ElevatorMusic.mp3");

        // Checking lobbies every 5 seconds
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (!inLobby) {
                communicator.send("LIST");
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        communicator.addListener(event -> {
            Platform.runLater(() -> {
                receiver(event);
            });
        });

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    logger.info("Quit");
                    communicator.send("QUIT");
                    Multimedia.stop();
                    gameWindow.startMenu();
                default:
                    break;
            }
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
        String[] data = message.split(" ", 2);

        String command = parts[0];
        String info = "";
        if (parts.length > 1) {
            info = parts[1];
        }


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
                startGameButton.setVisible(true);
                break;
            case "CHANNELS":
                fetchList(message);
                loadLobbies();
                logger.info(command + " " + info);
                break;
            case "NICK":
                // Changing username is managed server side
                logger.info("New User Name" + info);
                break;
            case "START":
                logger.info("Starting Multiplayer");
                gameWindow.startMultiplayer();
                break;
            case "PARTED":
                inLobby = false;
                showChatBox(false);
                break;
            case "USERS":
                break;
            case "MSG":
                showMessage(data[1]);
                break;
            case "ERROR":
                break;
            case "QUIT":
                break;
            default:
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

        chatBox.setMaxWidth(0.55*gameWindow.getWidth());
        chatBox.setMaxHeight(0.85*gameWindow.getHeight());

        lobbyPane.getChildren().add(chatBox);
        lobbyPane.setAlignment(chatBox, Pos.TOP_RIGHT);

        Text chatTextHeading = new Text("Game Chat:");
        chatTextHeading.getStyleClass().add("heading");
        chatBox.getChildren().add(chatTextHeading);

        StackPane textPane = new StackPane();
        textPane.setPrefHeight(gameWindow.getHeight()*0.5);
        textPane.setPrefWidth(gameWindow.getWidth()*0.4);
        textPane.getStyleClass().add("gameBox");
        textPane.getChildren().add(textflow);

        chatBox.getChildren().add(textPane);

        HBox messageInputContainer = new HBox();
        TextField messageField = new TextField();
        messageField.setPromptText("Type /nick (name)");
        Button sendMessageButton = new Button("Send");
        messageInputContainer.getChildren().addAll(messageField, sendMessageButton);

        messageInputContainer.setSpacing(10);
        startGameButton.setStyle("-fx-background-color: lime; -fx-text-fill: white");
        //startGameButton.setVisible(false);

        Button quitGameButton = new Button("Quit");
        quitGameButton.setStyle("-fx-background-color: red; -fx-text-fill: white");


        messageInputContainer.getChildren().addAll(startGameButton, quitGameButton);
        chatBox.getChildren().add(messageInputContainer);
        lobbyBox.setMaxWidth(0.45*gameWindow.getWidth());
        lobbyBox.setMaxHeight(0.85*gameWindow.getHeight());
        lobbyPane.setAlignment(lobbyBox, Pos.TOP_LEFT);

        chatBox.setVisible(false);

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

        quitGameButton.setOnAction(event -> {
            inLobby = false;
            showChatBox(false);
            communicator.send("PART");
        });

        sendMessageButton.setOnAction(event -> {
            String textInput = messageField.getText();
            sendMessage(textInput);
        });

        startGameButton.setOnAction(event -> {
            communicator.send("START");
        });

        lobbyListViewer.setOnMouseClicked(event -> {
            String selectedLobby = lobbyListViewer.getSelectionModel().getSelectedItem();
            //joinLobby(selectedLobby);
            communicator.send("JOIN " + selectedLobby);
        });
    }

    public void sendMessage(String text) {
        if (text.contains("/nick ")) {
            String parts[] = text.split(" ");
            communicator.send("NICK " + parts[1]);
        }
        communicator.send("MSG " + text);
    }

    public void showChatBox(boolean toggle) {
        chatBox.setVisible(toggle);
    }

    public void showMessage(String message) {
        String parts[] = message.split(":");
        String username = "[" +  parts[0] + "]";
        String contents = " > " + parts[1];
        Text send = new Text(username + contents + "\n");
        send.getStyleClass().add("messages");
        send.setFill(Color.WHITE);
        textflow.getChildren().add(send);
        if (textflow.getChildren().size() > 17) {
            textflow.getChildren().remove(0);
        }

    }

    public void createLobby(String lobbyName) {
        communicator.send("CREATE " + lobbyName);
        lobbyNames.add(lobbyName);
        loadLobbies();
    }

    public void joinLobby(String lobby) {
        inLobby = true;
        showChatBox(true);
        logger.info("Joining: " + lobby);

    }

    public void loadLobbies() {
        logger.info("Found (" + lobbyNames.toArray().length + ") Active Lobbies");
        lobbyListViewer.setItems(lobbyNames);

    }
}
