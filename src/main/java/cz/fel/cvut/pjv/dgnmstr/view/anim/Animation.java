package cz.fel.cvut.pjv.dgnmstr.view.anim;

import cz.fel.cvut.pjv.dgnmstr.global.ImgLoader;
import javafx.scene.image.Image;

/**
 * One strip of frames inside a sprite sheet.
 * Frames are laid out either along a row (default) or down a column.
 *
 * @param sheet resource path of the sprite sheet
 * @param frameWidth width of one frame in the sheet
 * @param frameHeight height of one frame in the sheet
 * @param line index of the row (or column when vertical) holding the frames
 * @param firstFrame index of the first frame inside the line
 * @param frameCount number of frames, or 0 to use the whole line
 * @param fps playback speed
 * @param vertical true when frames go down a column
 */
public record Animation(String sheet, int frameWidth, int frameHeight, int line,
                        int firstFrame, int frameCount, double fps, boolean vertical) {

    /**
     * Frames taken from one row of a sheet.
     */
    public static Animation row(String sheet, int frameWidth, int frameHeight, int row, int firstFrame, int frameCount, double fps) {
        return new Animation(sheet, frameWidth, frameHeight, row, firstFrame, frameCount, fps, false);
    }

    /**
     * Frames taken from one column of a sheet.
     */
    public static Animation column(String sheet, int frameWidth, int frameHeight, int column, int firstFrame, int frameCount, double fps) {
        return new Animation(sheet, frameWidth, frameHeight, column, firstFrame, frameCount, fps, true);
    }

    /**
     * A whole single-row image, frame count is taken from its width.
     */
    public static Animation strip(String sheet, int frameWidth, int frameHeight, double fps) {
        return new Animation(sheet, frameWidth, frameHeight, 0, 0, 0, fps, false);
    }

    public Image image() {
        return ImgLoader.sheet(sheet);
    }

    public int frames() {
        if (frameCount > 0) {
            return frameCount;
        }
        Image img = image();
        if (img == null) {
            return 1;
        }
        return Math.max(1, (int) (img.getWidth() / frameWidth));
    }

    public double duration() {
        return frames() / fps;
    }

    /**
     * Frame index shown after the given time.
     * @param time seconds since the animation started
     * @param loop whether to wrap around or stay on the last frame
     * @return index of the frame to draw
     */
    public int frameAt(double time, boolean loop) {
        int frames = frames();
        int frame = (int) Math.floor(Math.max(0, time) * fps);
        if (loop) {
            return frame % frames;
        }
        return Math.min(frame, frames - 1);
    }

    public double sourceX(int frame) {
        return vertical ? line * frameWidth : (firstFrame + frame) * frameWidth;
    }

    public double sourceY(int frame) {
        return vertical ? (firstFrame + frame) * frameHeight : line * frameHeight;
    }
}
