package uk.ac.soton.comp1206.game;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.GameLoopListener;

import java.util.Timer;

// Manages the timer and game loop is initialised in game loop - still a WIP
public class GameLoop extends java.util.TimerTask {
    private static final Logger logger = LogManager.getLogger(GameLoop.class);
    private Timer timer;
    private Game game;
    public GameLoopListener gameLoopListener;

    GameLoop(Timer timer, Game game) {
        this.timer = timer;
        this.game = game;
    }

    public void run() {
        logger.info("Game loop started");
        gameLoopListener.gameLooped(game.getTimerDelay());
        int currentLife = game.lives.get();
        currentLife--;
        game.lives.set(currentLife);
        reset(game.getTimerDelay());
    }

    public void reset(int delay) {
        cancel();
        timer.schedule(new GameLoop(timer, game), delay);
    }

    public void setGameLoopListener(GameLoopListener listener) {
        this.gameLoopListener = listener;
    }
}
