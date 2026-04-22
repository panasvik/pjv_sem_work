package cz.fel.cvut.pjv.dgnmstr.entity;

import cz.fel.cvut.pjv.dgnmstr.entity.hero.Hero;

public interface Interactable {
    void onInteract(Hero player);
}
