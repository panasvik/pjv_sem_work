package cz.fel.cvut.pjv.dgnmstr.global;

public enum ProjectileType {
    ARROW(ImgId.ARROW),
    FIREBALL(ImgId.FIREBALL),
    WATERBALL(ImgId.WATERBALL)
    ;
    private final ImgId imgId;

    ProjectileType(ImgId imgId) {
        this.imgId = imgId;
    }
    public ImgId getImgId() {
        return imgId;
    }
}
