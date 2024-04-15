package uk.ac.soton.comp1206.scene;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoreScene extends BaseScene {
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
    }
}
