package cz.fel.cvut.pjv.dgnmstr.entity.projectile;

import cz.fel.cvut.pjv.dgnmstr.view.ImgId;

public enum ProjectileType {
    ARROW(ImgId.ARROW),
    FIREBALL(ImgId.FIREBALL),
    WATERBALL(ImgId.WATERBALL);

    private final ImgId imgId;

    ProjectileType(ImgId imgId) {
        this.imgId = imgId;
    }
    public ImgId getImgId() {
        return imgId;
    }
}
