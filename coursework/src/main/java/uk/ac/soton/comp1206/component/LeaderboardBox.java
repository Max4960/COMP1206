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

/**
 * <p>LeaderboardBox class.</p>
 *
 * @author ASUS
 * @version $Id: $Id
 */
public class LeaderboardBox extends VBox {

    private static final Logger logger = LogManager.getLogger(LeaderboardBox.class);

    public SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();
    VBox scoreBox = new VBox();

    /**
     * <p>Constructor for LeaderboardBox.</p>
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
     * <p>Getter for the field <code>scores</code>.</p>
     *
     * @return a {@link javafx.beans.property.SimpleListProperty} object
     */
    public SimpleListProperty<Pair<String, Integer>> getScores() {
        return scores;
    }

    /**
     * <p>updateScores.</p>
     */
    public void updateScores() {
        logger.info("Updating scores");
        scoreBox.getChildren().clear();

        for (Pair pair : scores) {
            Text text = new Text(pair.getKey() + " " + pair.getValue());
            text.getStyleClass().add("hiscore");
            scoreBox.getChildren().add(text);
        }
    }

}
