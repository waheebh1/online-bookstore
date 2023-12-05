/**
 * Inventory Item Object
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
     * @author Maisha Abdullah
     */
    public InventoryItem(){
    }

    /**
     * Constructor for InventoryItem
     * @param book      Book that is the item
     * @param quantity  Quantity of that item
     * @author Maisha Abdullah
     */
    public InventoryItem (Book book, int quantity){
        super(book,quantity);
    }

    public InventoryItem (Book book, int quantity, Inventory inventory){
        super(book,quantity);
        this.inventory = inventory;
    }

    /**
     * Get inventory that the item belongs to
     * @return inventory
     * @author Maisha Abdullah
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Set inventory
     * @param inventory inventory
     * @author Maisha Abdullah
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
