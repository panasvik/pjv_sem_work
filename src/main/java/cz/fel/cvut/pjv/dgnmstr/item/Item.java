package cz.fel.cvut.pjv.dgnmstr.item;

import cz.fel.cvut.pjv.dgnmstr.player.Inventory;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cz.fel.cvut.pjv.dgnmstr.player.PlayerProfile;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@class"
)
/**
 * Base class for everything that can exist as an item. Uses double
 * dispatch (addTo/removeFrom/acceptUsing) for inventory operations.
 */
public abstract class Item implements Serializable {
    private String name;
    private int price;

    /**
     * Creates a new Item.
     * @param name display name
     * @param price price value
     */
    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }

    protected Item() {
        this.name = "forJSON";
        this.price = 0;
    }

    public abstract boolean addTo(Inventory inventory);
    public abstract boolean removeFrom(Inventory inventory);
    public abstract boolean acceptUsing(PlayerProfile profile);
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