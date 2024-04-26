package uk.ac.soton.comp1206.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

/**
 * <p>Multimedia class.</p>
 *
 * @author ASUS
 * @version $Id: $Id
 */
public class Multimedia {
    /** Constant <code>logger</code> */
    public static final Logger logger = LogManager.getLogger(GamePane.class);

    private static MediaPlayer audioPlayer;
    private static MediaPlayer musicPlayer;

    /**
     * <p>playAudio.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public static void playAudio(String name) {
        String filePath = String.valueOf(Multimedia.class.getResource("/sounds/" + name));
        audioPlayer = new MediaPlayer(new Media(filePath));
        audioPlayer.play();
    }

    /**
     * <p>playMusic.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public static void playMusic(String name) {
        String filePath = String.valueOf(Multimedia.class.getResource("/music/" + name));
        musicPlayer = new MediaPlayer(new Media(filePath));
        musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(musicPlayer.getStartTime()));
        musicPlayer.play();
    }

    /**
     * <p>stop.</p>
     */
    public static void stop() {
        musicPlayer.stop();
    }
}
