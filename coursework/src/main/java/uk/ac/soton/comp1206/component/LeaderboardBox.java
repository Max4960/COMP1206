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

public class LeaderboardBox extends VBox {

    private static final Logger logger = LogManager.getLogger(LeaderboardBox.class);

    public SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

    public LeaderboardBox() {
        scores.addListener((ListChangeListener<? super Pair<String, Integer>>) (c) -> {
            this.updateScores();
        });
        Text leaderboard = new Text("Leaderboard");
        leaderboard.getStyleClass().add("hiscore");
        getChildren().add(leaderboard);
    }

    public SimpleListProperty<Pair<String, Integer>> getScores() {
        return scores;
    }

    public void updateScores() {
        logger.info("Updating scores");
        for (Pair pair : scores) {
            Text text = new Text(pair.getKey() + " " + pair.getValue());
            text.getStyleClass().add("hiscore");
            getChildren().add(text);
        }
    }

}
