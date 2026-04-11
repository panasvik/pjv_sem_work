package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class BasicWarriorChestplate extends ArmorPiece{
    public BasicWarriorChestplate() {
        super(ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getName(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getPrice(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getRole(),
                ArmorPieceType.CHESTPLATE,
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getSpeed(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getDexterity(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getArmor(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getHp()
                );
    }
}
