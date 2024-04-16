package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);
    private Game game;
    private boolean printable = false;

    ArrayList<Pair<String,Integer>> localScoresList = new ArrayList<Pair<String,Integer>>();
    SimpleListProperty<Pair<String,Integer>> localScores = new SimpleListProperty<Pair<String,Integer>>(FXCollections.observableList(localScoresList));
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoreScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
    }

    @Override
    public void initialise() {

    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var scorePane = new StackPane();
        scorePane.setMaxWidth(gameWindow.getWidth());
        scorePane.setMaxHeight(gameWindow.getHeight());
        scorePane.getStyleClass().add("info-background");
        root.getChildren().add(scorePane);

        Text gameOverText = new Text("GAME OVER");
        gameOverText.getStyleClass().add("bigtitle");
        scorePane.getChildren().add(gameOverText);

        boolean loaded = false;
        try {
            loadScores();
            for (Pair<String,Integer> score : localScoresList) {
                if (score.getValue() < game.score.get()) {
                    VBox getPlayerInfoBox = new VBox();
                    getPlayerInfoBox.setAlignment(Pos.CENTER);
                    scorePane.getChildren().add(getPlayerInfoBox);
                    // TODO: Fix a highscore replacing the wrong previous high score
                    Text enterName = new Text("Enter Your Name:");
                    enterName.getStyleClass().add("menuItem");
                    getPlayerInfoBox.getChildren().add(enterName);

                    TextField enterNameText = new TextField();
                    getPlayerInfoBox.getChildren().add(enterNameText);
                    Button enterNameButton = new Button("Enter Name");
                    getPlayerInfoBox.getChildren().add(enterNameButton);
                    enterNameButton.setOnAction(e -> {
                        String playerName = enterNameText.getText();
                        localScoresList.add(new Pair<>(playerName, game.score.get()));
                        enterNameText.setVisible(false);
                        enterNameButton.setVisible(false);
                        enterName.setVisible(false);

                        sortScores();
                        try {
                            writeScores();
                        } catch (IOException e1) {
                            throw new RuntimeException(e1);
                        }

                        VBox localVBox = new VBox();
                        localVBox.setAlignment(Pos.BOTTOM_LEFT);
                        scorePane.getChildren().add(localVBox);
                        Text localScoreText = new Text("Local Scores");
                        localScoreText.getStyleClass().add("menuItem");
                        localVBox.getChildren().add(localScoreText);
                        int counter = 0;
                        for (Pair<String, Integer> pair : localScores) {
                            if (counter >= 10) {
                                break;
                            }
                            Text scoreText = new Text(pair.getKey() + " " + pair.getValue().toString());
                            scoreText.getStyleClass().add("scorelist");
                            localVBox.getChildren().add(scoreText);
                        }
                    });
                    break; // DO NOT DELETE
                }
            }
        } catch (IOException e) {
            logger.info("Failed to load the score file");
            throw new RuntimeException(e);

        }
    }

    public void loadScores() throws IOException {
        String fileName = "coursework/scores.txt";
        File file = new File(fileName);
        if (file.exists()) {
            FileReader fr = new FileReader("coursework/scores.txt");
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                localScoresList.add(new Pair<>(parts[0],Integer.parseInt(parts[1])));
            }

        } else {
            logger.info("File does not exist");
            return;
        }
    }

    public void sortScores() {  // Originally called writeScores - Made into a separate method
        // Sorting by integer values
        // Used Java Docs https://docs.oracle.com/javase/8/docs/api/java/util/Collections.html#sort-java.util.List-java.util.Comparator-
        // https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#compare-T-T-
        Collections.sort(localScoresList, new Comparator<Pair<String,Integer>>() {
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
        localScoresList = new ArrayList<>(localScores.subList(0,10));
        logger.info(String.format("Writing scores to file: %s", localScoresList));
    }

    public void writeScores() throws IOException {
        sortScores();
        String fileName = "coursework/scores.txt";
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (int i = 0; i < 10; i++) {
                    writer.write("NULL:0");
                }
            } catch (IOException e) {
                logger.info("Failed to create scores file");
            }
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                for (Pair<String,Integer> pair : localScoresList) {
                    writer.write(pair.getKey() + ":" + pair.getValue().toString()+"\n");
                }
            } catch (IOException e) {
                logger.info("Failed to write scores to file");
            }
        }
    }
}
