package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.ui.Multimedia;

public class InstructionsScene extends BaseScene{
    private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

    public InstructionsScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instructions Scene");
    }

    @Override
    public void build() {
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var infoPane = new StackPane();
        infoPane.setMaxWidth(gameWindow.getWidth());
        infoPane.setMaxHeight(gameWindow.getHeight());
        infoPane.getStyleClass().add("info-background");
        root.getChildren().add(infoPane);

        // Image is 1368x846 pixels
        Image instructions = new Image(String.valueOf(Multimedia.class.getResource("/images/Instructions.png")));
        ImageView instructionView = new ImageView(instructions);
        instructionView.setFitWidth(342);
        instructionView.setFitHeight(212);
        instructionView.setTranslateY(-180);
        infoPane.getChildren().add(instructionView);

        GridPane pieces = new GridPane();
        pieces.setHgap(10);
        pieces.setVgap(10);
        pieces.setTranslateY(-10);
        pieces.setAlignment(Pos.BOTTOM_CENTER);
        infoPane.getChildren().add(pieces);

        int x = 0;
        int y = 0;

        for (int i = 0; i <= 14; i++) { // Loop through the 15 pieces
            GamePiece piece = GamePiece.createPiece(i);
            PieceBoard pb = new PieceBoard(3,3,100,100);
            pb.setPiece(piece);
            pieces.add(pb, x , y);
            x++;
            if (x == 5) {   // Want to create a new row every 5 elements
                y++;        // Setting y = x/5 produces a diagonal pattern, as x needs to be reset
                x = 0;
            }
        }
    }

    @Override
    public void initialise() {
        // Checks if ESC has been pressed
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    gameWindow.startMenu();
                default:
                    break;
            }
        });
    }
}
