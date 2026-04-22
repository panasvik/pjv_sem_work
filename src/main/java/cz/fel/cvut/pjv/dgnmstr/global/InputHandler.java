package cz.fel.cvut.pjv.dgnmstr.global;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class InputHandler {

    private final Scene scene;
    private final Set<KeyCode> pressedKeys = new HashSet<>();

    private double mouseX = 0;
    private double mouseY = 0;
    private boolean leftMousePressed = false;

    private boolean ult = false;
    private boolean interact = false;

    private static final Logger log = Logger.getLogger(InputHandler.class.getName());

    public InputHandler(Scene scene) {
        this.scene = scene;
        setInputHandlers();
    }

    private void setInputHandlers() {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            pressedKeys.add(code);

            switch (code) {
                case E:
                    ult = true;
                    break;
                case F:
                    interact = true;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            pressedKeys.remove(event.getCode());
        });

        scene.setOnMouseMoved(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            mouseX = event.getSceneX();
            mouseY = event.getSceneY();
        });

        scene.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                leftMousePressed = true;
            }
        });

        scene.setOnMouseReleased(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                leftMousePressed = false;
            }
        });
    }


    public Inputs getInputs() {

        double sceneCenterX = scene.getWidth() / 2;
        double sceneCenterY = scene.getHeight() / 2;

        Inputs currentInputs = new Inputs(
                ult,
                interact,
                leftMousePressed, // Передаем атаку!
                pressedKeys.contains(KeyCode.W),
                pressedKeys.contains(KeyCode.S), // Исправлено на S
                pressedKeys.contains(KeyCode.A), // Исправлено на A
                pressedKeys.contains(KeyCode.D), // Исправлено на D
                mouseX - sceneCenterX,
                mouseY - sceneCenterY
        );

        ult = false;
        interact = false;

        return currentInputs;
    }
}