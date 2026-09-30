package cz.fel.cvut.pjv.dgnmstr.view;

public enum ImgId {
    GOBLIN("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinScout-Sheet.png", 600, 500, 2),
    ZOMBIE("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Zombie.png", 32, 32, 2),
    EVIL_WIZARD("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/WizardIdle.png", 150, 150, 6),
    GHOST("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Ghost.png", 32, 32, 2.2),
    SKELETON("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinArcher-Sheet.png", 600, 500, 2),
    WARRIOR("/cz/fel/cvut/pjv/dgnmstr/entity/hero/Down/Png/WarriorDownIdle.png", 48, 48,3),
    WALL("/cz/fel/cvut/pjv/dgnmstr/entity/map/Wall.png", 32, 32, 1),
    FENCE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Fence.png", 16, 16, 1),
    GOLD("/cz/fel/cvut/pjv/dgnmstr/entity/map/unpaid.png", 32, 32, 1),
    CHEST("/cz/fel/cvut/pjv/dgnmstr/entity/map/Chest.png", 32, 32, 1),
    GRATE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Grate.png", 32, 32, 1),
    TILE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Tile.png", 32, 32, 1),
    ANYSEK("/cz/fel/cvut/pjv/dgnmstr/entity/boss/Behemoth.png", 128, 128, 6),
    DWIGHT("/cz/fel/cvut/pjv/dgnmstr/entity/boss/leviathan.png", 612, 648, 5),
    ARROW("/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/Arrow.png", 16, 16, 2),
    FIREBALL("/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/Fireball1.png", 16, 16, 2),
    WATERBALL("/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/Waterball.png", 48, 48, 1),
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
