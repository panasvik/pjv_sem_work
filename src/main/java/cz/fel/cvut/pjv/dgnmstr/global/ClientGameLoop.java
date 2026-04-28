package cz.fel.cvut.pjv.dgnmstr.global;

import cz.fel.cvut.pjv.dgnmstr.dungeon.controllers.LevelController;
import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;
import cz.fel.cvut.pjv.dgnmstr.view.GameRenderer;
import javafx.animation.AnimationTimer;

import java.util.logging.Logger;

public class ClientGameLoop extends AnimationTimer {
    private long lastUPDTime = 0;

    private final GameEngine engine;
    private final InputHandler inputHandler;
    private final GameRenderer renderer;
    private final Hero localPlayer; // hero model
    private int counter = 0;
    private static final Logger log = Logger.getLogger(ClientGameLoop.class.getName());


    public ClientGameLoop(GameEngine engine, InputHandler inputHandler, GameRenderer renderer, Hero localPlayer) {
        this.engine = engine;
        this.inputHandler = inputHandler;
        this.renderer = renderer;
        this.localPlayer = localPlayer;
    }

    @Override
    public void handle(long now) {
        counter++;
        if (lastUPDTime == 0) {
            lastUPDTime = now;
            return;
        }
        double deltaTime = (now - lastUPDTime) / 1_000_000_000.0;
        lastUPDTime = now;

        // take inputs and send to player
        Inputs currentInputs = inputHandler.getInputs();
        localPlayer.setInputs(currentInputs);

        // sending data to server in future updates
        // ...

        engine.tick(deltaTime);
        if (counter % 180 == 0) {
            System.out.println("local player position: " + localPlayer.getX()+ localPlayer.getX());
        }

        // rendering when GameRenderer is ready
        //renderer.render(GameManager.getInstance().getLevel());
    }

    @Override
    public void stop() {
        super.stop();
        lastUPDTime = 0;
    }
}