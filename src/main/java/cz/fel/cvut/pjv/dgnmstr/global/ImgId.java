package cz.fel.cvut.pjv.dgnmstr.global;

public enum ImgId {
    GOBLIN("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinScout-Sheet.png", 512, 512),
    ZOMBIE("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Zombie.png", 32, 32),
    EVIL_WIZARD("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/WizardIdle.png", 32, 32),
    GHOST("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Ghost.png", 32, 32),
    SKELETON("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinArcher-Sheet.png", 512, 512);

    private final String imgPath;
    private final int frameWidth;
    private final int frameHeight;
    private double width;
    private double height;

    ImgId(String imgPath, int frameWidth, int frameHeight) {
        this.imgPath = imgPath;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
    }

    public double getHeight() {
        return height;
    }

    public String getImgPath() {
        return imgPath;
    }

    public double getWidth() {
        return width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameWidth() {
        return frameWidth;
    }
}
