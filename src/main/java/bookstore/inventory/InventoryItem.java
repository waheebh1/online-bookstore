/**
 * Inventory Item Object
 * @author Maisha Abdullah
 */

package bookstore.inventory;

import jakarta.persistence.*;

@Entity
public class InventoryItem extends Item{


    @ManyToOne
    @JoinColumn (name = "inventory_id")
    private Inventory inventory;

    /**
     * Default constructor
     */
    public InventoryItem(){
    }

    /**
     * Constructor for InventoryItem
     * @param book      Book that is the item
     * @param quantity  Quantity of that item
     */
    public InventoryItem (Book book, int quantity){
        super(book,quantity);
    }

    public InventoryItem (Book book, int quantity, Inventory inventory){
        super(book,quantity);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
