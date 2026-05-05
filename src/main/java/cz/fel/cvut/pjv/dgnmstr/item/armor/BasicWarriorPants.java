package cz.fel.cvut.pjv.dgnmstr.item.armor;

public class BasicWarriorPants extends ArmorPiece{
    public BasicWarriorPants() {
        super(ArmorPieceStats.BASIC_WARRIOR_PANTS.getName(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getPrice(),
                ArmorPieceStats.BASIC_WARRIOR_CHESTPLATE.getRole(),
                ArmorPieceType.PANTS,
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getSpeed(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getDexterity(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getArmor(),
                ArmorPieceStats.BASIC_WARRIOR_PANTS.getHp()
        );
    }
}
