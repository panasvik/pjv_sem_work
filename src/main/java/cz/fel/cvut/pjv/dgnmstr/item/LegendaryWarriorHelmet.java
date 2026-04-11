package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class LegendaryWarriorHelmet extends ArmorPiece{
    public LegendaryWarriorHelmet() {
        super(ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getName(),
                ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getPrice(),
                ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getRole(),
                ArmorPieceType.HELMET,
                ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getSpeed(),
                ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getDexterity(),
                ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getArmor(),
                ArmorPieceStats.LEGENDARY_WARRIOR_HELMET.getHp()
        );
    }
}
