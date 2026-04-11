package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.entity.Role;

public class BasicWarriorPants extends ArmorPiece{
    public BasicWarriorPants() {
        super(ArmorPieceStats.BASIC_WARRIOR_PANTS.getName(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getPrice(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getRole(),
                ArmorPieceType.CHESTPLATE,
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getSpeed(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getDexterity(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getArmor(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getHp()
        );
    }
}
