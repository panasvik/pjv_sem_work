package cz.fel.cvut.pjv.dgnmstr.item.armor;


import cz.fel.cvut.pjv.dgnmstr.entity.hero.Role;
import cz.fel.cvut.pjv.dgnmstr.item.AddableItem;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;
/**
 * Armor piece.
 */
public abstract class ArmorPiece extends AddableItem {
    private int hp, defence, dexterity, speed;
    private final ArmorPieceType type;
    private final Role role;

    /**
     * Creates a new ArmorPiece.
     *
     * @param name display name
     * @param price price value
     * @param role the chosen role
     * @param type the type
     * @param speed movement speed
     * @param dexterity dexterity
     * @param defence defence
     * @param hp hit points value
     */
    public ArmorPiece(String name, int price, Role role, ArmorPieceType type, int speed, int dexterity, int defence, int hp) {
        super(name, price);
        this.role = role;
        this.type = type;
        this.speed = speed;
        this.dexterity = dexterity;
        this.defence = defence;
        this.hp = hp;
    }
    protected ArmorPiece() {
        super();
        this.role = Role.WARRIOR;
        this.type = ArmorPieceType.CHESTPLATE;
        this.speed = 0;
        this.dexterity = 0;
        this.defence = 0;
        this.hp = 0;
    }

    @Override
    public boolean acceptUsing(PlayerProfile profile) {
        return profile.equipArmor(this);
    }

    public int getHp() {
        return hp;
    }

    public int getDefence() {
        return defence;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getSpeed() {
        return speed;
    }

    public Role getRole() {
        return role;
    }

    public ArmorPieceType getType() {
        return type;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}