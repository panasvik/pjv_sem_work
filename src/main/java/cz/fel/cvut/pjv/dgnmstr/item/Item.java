package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type" // имя поля в JSON
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Weapon.class, name = "weapon"),
        @JsonSubTypes.Type(value = ArmorPiece.class, name = "armor")
})
public abstract class Item {
    private String name;
    private int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public abstract boolean addTo(Inventory inventory);
    public abstract boolean removeFrom(Inventory inventory);

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}