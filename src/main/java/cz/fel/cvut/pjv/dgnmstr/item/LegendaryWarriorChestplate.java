package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class LegendaryWarriorChestplate extends ArmorPiece{
    public LegendaryWarriorChestplate() {
        super(ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getName(),
                ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getPrice(),
                ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getRole(),
                ArmorPieceType.CHESTPLATE,
                ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getSpeed(),
                ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getDexterity(),
                ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getArmor(),
                ArmorPieceStats.LEGENDARY_WARRIOR_CHESTPLATE.getHp()
        );
    }
}
