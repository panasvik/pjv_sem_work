package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class LegendaryWarriorPants extends ArmorPiece{
    public LegendaryWarriorPants() {
        super(ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getName(),
                ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getPrice(),
                ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getRole(),
                ArmorPieceType.PANTS,
                ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getSpeed(),
                ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getDexterity(),
                ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getArmor(),
                ArmorPieceStats.LEGENDARY_WARRIOR_PANTS.getHp()
        );
    }
}
