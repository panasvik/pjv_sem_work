package cz.fel.cvut.pjv.dgnmstr.entity;
/**
 * Side an entity belongs to. Used to decide who is hostile to whom
 * during combat. Neutral entities are never hostile.
 */
public enum Faction {
    HERO,
    MONSTER,
    NEUTRAL
}