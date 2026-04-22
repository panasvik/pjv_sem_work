package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Warrior;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class WarriorController {

    private final Warrior warrior;
    private final Scene scene;

    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private double mouseX = 0;
    private double mouseY = 0;
    private boolean leftMousePressed = false;

    private static final Logger log = Logger.getLogger(WarriorController.class.getName());

    public WarriorController(Warrior warrior, Scene scene) {
        this.warrior = warrior;
        this.scene = scene;
        setInputHandlers();
    }

    private void setInputHandlers() {

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            pressedKeys.add(code);

            switch (code) {
                case E:
                    warrior.setToUltimate(true);
                    log.info("Ultimate activated");
                    break;
                case F:
                    warrior.setToInteract(true);
                    log.info("Interact key pressed");
                    break;
            }

            updateMovement();
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
            updateMovement();
        });


        scene.setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            updateLookDirection();
        });

        scene.setOnMouseDragged(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
            updateLookDirection();
        });

        scene.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                leftMousePressed = true;
                warrior.setToAttack(true);
                log.fine("Attack initiated");
            }
        });

        scene.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                leftMousePressed = false;
            }
        });
    }

    private void updateMovement() {
        warrior.setUp(pressedKeys.contains(KeyCode.W));
        warrior.setDown(pressedKeys.contains(KeyCode.S));
        warrior.setLeft(pressedKeys.contains(KeyCode.A));
        warrior.setRight(pressedKeys.contains(KeyCode.D));
    }

    private void updateLookDirection(){
        warrior.setLookTarget(mouseX, mouseY);
    }
}
