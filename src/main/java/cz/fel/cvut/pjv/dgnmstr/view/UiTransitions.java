package cz.fel.cvut.pjv.dgnmstr.view;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Small helpers that open and close UI panels with a fade and slide
 * instead of making them pop in and out.
 */
public final class UiTransitions {
    private static final Duration SHOW = Duration.millis(220);
    private static final Duration HIDE = Duration.millis(160);
    private static final String RUNNING = "dm-transition";
    private static final String HIDING = "dm-hiding";

    private UiTransitions() {
    }

    /**
     * Whether the panel is shown and not currently closing.
     */
    public static boolean isShown(Node node) {
        return node.isVisible() && !Boolean.TRUE.equals(node.getProperties().get(HIDING));
    }

    /**
     * Fades and slides a panel in.
     */
    public static void show(Node node) {
        if (isShown(node)) {
            return;
        }
        stopRunning(node);
        node.getProperties().put(HIDING, false);
        node.setVisible(true);

        FadeTransition fade = new FadeTransition(SHOW, node);
        fade.setFromValue(node.getOpacity() < 1 ? node.getOpacity() : 0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(SHOW, node);
        slide.setFromY(14);
        slide.setToY(0);
        ScaleTransition scale = new ScaleTransition(SHOW, node);
        scale.setFromX(0.96);
        scale.setFromY(0.96);
        scale.setToX(1);
        scale.setToY(1);
        ParallelTransition all = new ParallelTransition(fade, slide, scale);
        all.setInterpolator(Interpolator.EASE_OUT);
        play(node, all);
    }

    /**
     * Fades and slides a panel out, then hides it.
     */
    public static void hide(Node node) {
        if (!isShown(node)) {
            return;
        }
        stopRunning(node);
        node.getProperties().put(HIDING, true);

        FadeTransition fade = new FadeTransition(HIDE, node);
        fade.setToValue(0);
        TranslateTransition slide = new TranslateTransition(HIDE, node);
        slide.setToY(10);
        ParallelTransition all = new ParallelTransition(fade, slide);
        all.setInterpolator(Interpolator.EASE_IN);
        all.setOnFinished(e -> {
            node.setVisible(false);
            node.setOpacity(1);
            node.setTranslateY(0);
            node.getProperties().put(HIDING, false);
        });
        play(node, all);
    }

    /**
     * Hides a panel immediately, without animation.
     */
    public static void hideNow(Node node) {
        stopRunning(node);
        node.setVisible(false);
        node.setOpacity(1);
        node.setTranslateY(0);
        node.setScaleX(1);
        node.setScaleY(1);
        node.getProperties().put(HIDING, false);
    }

    private static void play(Node node, ParallelTransition transition) {
        node.getProperties().put(RUNNING, transition);
        transition.play();
    }

    private static void stopRunning(Node node) {
        Object running = node.getProperties().get(RUNNING);
        if (running instanceof ParallelTransition transition) {
            transition.stop();
        }
    }
}
