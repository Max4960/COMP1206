package uk.ac.soton.comp1206.component;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.MultiplayerGame;

import java.util.ArrayList;

/**
 * Extends a VBox and is used for storing the scores of the users in the game
 */
public class LeaderboardBox extends VBox {

    private static final Logger logger = LogManager.getLogger(LeaderboardBox.class);

    /**
     * Simple list property of the scores of the players
     */
    public SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

    /**
     * Array list to keep track of dead players
     */
    ArrayList<String> theFallen = new ArrayList<>();

    // Holds scores
    VBox scoreBox = new VBox();

    /**
     * Constructor for LeaderboardBox
     */
    public LeaderboardBox() {
        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) (c) -> {
            this.updateScores();
        });
        Text leaderboard = new Text("Leaderboard");
        leaderboard.getStyleClass().add("heading");
        getChildren().add(leaderboard);
        getChildren().add(scoreBox);
    }

    /**
     * Getter for the scores simple list property
     * Is also used to bind the score value
     * @return a simple list property representing scores
     */
    public SimpleListProperty<Pair<String, Integer>> getScores() {
        return scores;
    }

    /**
     * Adds a player to list of dead players
     * @param name name of dead player
     */
    public void kill(String name) {
        theFallen.add(name);
    }

    /**
     * Updates the leaderboard
     */
    public void updateScores() {
        logger.info("Updating scores");
        scoreBox.getChildren().clear();

        for (Pair pair : scores) {
            Text text = new Text(pair.getKey() + " " + pair.getValue());
            if (theFallen.contains(pair.getKey())) {    // A dead player
                text.getStyleClass().add("deadhiscore");
            } else {    // A living player
                text.getStyleClass().add("hiscore");
            }
            scoreBox.getChildren().add(text);
        }
    }

}
