package uk.ac.soton.comp1206.ui;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;

public class Multimedia {
    public static final Logger logger = LogManager.getLogger(GamePane.class);

    private static MediaPlayer audioPlayer;
    private static MediaPlayer musicPlayer;

    public static void playAudio(String name) {
        String filePath = String.valueOf(Multimedia.class.getResource("/sounds/" + name));
        audioPlayer = new MediaPlayer(new Media(filePath));
        audioPlayer.play();
    }

    public static void playMusic(String name) {
        String filePath = String.valueOf(Multimedia.class.getResource("/music/" + name));
        musicPlayer = new MediaPlayer(new Media(filePath));
        musicPlayer.setOnEndOfMedia(() -> musicPlayer.seek(musicPlayer.getStartTime()));
        musicPlayer.play();
    }

    public static void stop() {
        musicPlayer.stop();
    }
}
