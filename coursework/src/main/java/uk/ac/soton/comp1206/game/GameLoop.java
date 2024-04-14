package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Timer;

public class GameLoop extends java.util.TimerTask {
    private static final Logger logger = LogManager.getLogger(GameLoop.class);
    private Timer timer;
    private Game game;

    GameLoop(Timer timer, Game game) {
        this.timer = timer;
        this.game = game;
    }

    public void run() {
        logger.info("Game loop started");
        reset(game.getTimerDelay());
    }

    public void reset(int delay) {
        cancel();
        timer.schedule(new GameLoop(timer, game), delay);
    }
}
