package uk.ac.soton.comp1206.scene;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

/**
 * <p>SplashScene class.</p>
 *
 * @author ASUS
 * @version $Id: $Id
 */
public class SplashScene extends BaseScene{

    /**
     * <p>Constructor for SplashScene.</p>
     *
     * @param gameWindow a {@link uk.ac.soton.comp1206.ui.GameWindow} object
     */
    public SplashScene(GameWindow gameWindow) {
        super(gameWindow);

    }

    /** {@inheritDoc} */
    @Override
    public void initialise() {

    }

    /** {@inheritDoc} */
    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.setAlignment(Pos.CENTER);
        root.getChildren().add(menuPane);


        Image splashImage = new Image(String.valueOf(Multimedia.class.getResource("/images/splashLogo.png")));
        ImageView splashView = new ImageView(splashImage);
        splashView.setFitWidth(gameWindow.getWidth()/2);
        splashView.setFitHeight(gameWindow.getHeight()/2);
        splashView.setPreserveRatio(true);
        menuPane.getChildren().add(splashView);

        AnimationTimer timer = new AnimationTimer() {
            double opacity = 0.0;
            @Override
            public void handle(long l) {
                opacity += 0.01;
                if (opacity >= 1) {
                    opacity = 1;
                    stop();
                }
                splashView.setOpacity(opacity);
            }
        };
        timer.start();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            Platform.runLater(() -> {
                gameWindow.startMenu();
            });
        }));
        timeline.play();
    }
}
