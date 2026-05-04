package cz.fel.cvut.pjv.dgnmstr.global;

public enum ImgId {
    GOBLIN("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinScout-Sheet.png", 512, 512, 1),
    ZOMBIE("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Zombie.png", 32, 32, 1),
    EVIL_WIZARD("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/WizardIdle.png", 150, 150, 3),
    GHOST("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Ghost.png", 32, 32, 1),
    SKELETON("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinArcher-Sheet.png", 512, 512,1),
    WARRIOR("/cz/fel/cvut/pjv/dgnmstr/entity/hero/Down/Png/WarriorDownIdle.png", 48, 48,3),
    FENCE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Fence.png", 32, 32, 1),
    GOLD("/cz/fel/cvut/pjv/dgnmstr/entity/map/unpaid.png", 32, 32, 1),
    CHEST("/cz/fel/cvut/pjv/dgnmstr/entity/map/Chest.png", 32, 32, 1),
    GRATE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Grate.png", 32, 32, 1),
    TILE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Tile.png", 32, 32, 1),
    ANYSEK("/cz/fel/cvut/pjv/dgnmstr/entity/boss/Behemoth.png", 96, 96, 5),
    DWIGHT("/cz/fel/cvut/pjv/dgnmstr/entity/boss/leviathan.png", 600, 600, 5)
    ;

    private final String imgPath;
    private final int frameWidth;
    private final int frameHeight;
    private final double scale;
    private double width;
    private double height;

    ImgId(String imgPath, int frameWidth, int frameHeight, double scale) {
        this.imgPath = imgPath;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.scale = scale;
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

    public double getScale() {
        return scale;
    }
}
