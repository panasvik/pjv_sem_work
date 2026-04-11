package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class BasicWarriorHelmet extends ArmorPiece{
    public BasicWarriorHelmet() {
        super(ArmorPieceStats.BASIC_WARRIOR_HELMET.getName(),
                ArmorPieceStats.BASIC_WARRIOR_HELMET.getPrice(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getRole(),
                ArmorPieceType.CHESTPLATE,
                ArmorPieceStats.BASIC_WARRIOR_HELMET.getSpeed(),
                ArmorPieceStats.BASIC_WARRIOR_HELMET.getDexterity(),
                ArmorPieceStats.BASIC_WARRIOR_HELMET.getArmor(),
                ArmorPieceStats.BASIC_WARRIOR_HELMET.getHp()
        );
    }
}
