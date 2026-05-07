package cz.fel.cvut.pjv.dgnmstr.global;

public enum ImgId {
    GOBLIN("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinScout-Sheet.png", 512, 512, 2, 52),
    ZOMBIE("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Zombie.png", 32, 32, 2, 52),
    EVIL_WIZARD("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/WizardIdle.png", 150, 150, 6, 52),
    GHOST("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/Ghost.png", 32, 32, 2.2, 52),
    SKELETON("/cz/fel/cvut/pjv/dgnmstr/entity/mobs/GoblinArcher-Sheet.png", 512, 512,2, 52),
    WARRIOR("/cz/fel/cvut/pjv/dgnmstr/entity/hero/Down/Png/WarriorDownIdle.png", 48, 48,3, 56),
    FENCE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Fence.png", 32, 32, 1, 28),
    GOLD("/cz/fel/cvut/pjv/dgnmstr/entity/map/unpaid.png", 32, 32, 1, 20),
    CHEST("/cz/fel/cvut/pjv/dgnmstr/entity/map/Chest.png", 32, 32, 1,28),
    GRATE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Grate.png", 32, 32, 1, 28),
    TILE("/cz/fel/cvut/pjv/dgnmstr/entity/map/Tile.png", 32, 32, 1,0),
    ANYSEK("/cz/fel/cvut/pjv/dgnmstr/entity/boss/Behemoth.png", 96, 96, 5,96),
    DWIGHT("/cz/fel/cvut/pjv/dgnmstr/entity/boss/leviathan.png", 600, 600, 5,96),
    ARROW("/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/Arrow.png", 16, 16, 2, 16),
    FIREBALL("/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/Fireball1.png", 16, 16, 2, 16),
    WATERBALL("/cz/fel/cvut/pjv/dgnmstr/entity/projectiles/Waterball.png", 48, 48, 1, 30),
    ;

    private final String imgPath;
    private final int frameWidth;
    private final int frameHeight;
    private final int hitBoxSize;
    private final double scale;
    private double width;
    private double height;

    ImgId(String imgPath, int frameWidth, int frameHeight, double scale, int hitBoxSize) {
        this.imgPath = imgPath;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.scale = scale;
        this.hitBoxSize = hitBoxSize;
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

    public int getHitBoxSize() {
        return hitBoxSize;
    }
}
