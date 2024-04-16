package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.*;
import java.util.ArrayList;

public class ScoreScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ScoreScene.class);

    ArrayList<Pair<String,Integer>> localScoresList = new ArrayList<Pair<String,Integer>>();
    SimpleListProperty<Pair<String,Integer>> localScores = new SimpleListProperty<Pair<String,Integer>>(FXCollections.observableList(localScoresList));
    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoreScene(GameWindow gameWindow) {
        super(gameWindow);
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

        VBox localVBox = new VBox();
        localVBox.setAlignment(Pos.BOTTOM_LEFT);
        scorePane.getChildren().add(localVBox);
        Text localScoreText = new Text("Local Scores");
        localScoreText.getStyleClass().add("menuItem");
        localVBox.getChildren().add(localScoreText);
        try {
            loadScores();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Pair<String,Integer> pair : localScores) {
            Text scoreText = new Text(pair.getKey() + " " + pair.getValue().toString());
            scoreText.getStyleClass().add("scorelist");
            localVBox.getChildren().add(scoreText);
        }
    }

    public void loadScores() throws IOException {
        //File scoresFile = new File("scores.txt");

        FileReader fr = new FileReader("coursework/scores.txt");
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(":");
            localScoresList.add(new Pair<>(parts[0],Integer.parseInt(parts[1])));
        }
    }
}
