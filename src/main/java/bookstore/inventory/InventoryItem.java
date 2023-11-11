/**
 * Inventory Item Object
 * @author Maisha Abdullah
 */

package bookstore.inventory;

import jakarta.persistence.*;

@Entity
public class InventoryItem {

    @OneToOne
    private Book book;
    private int quantity;

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    private Long id;

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
        this.book = book;
        this.quantity = quantity;
    }

    /**
     * Method to set the ID
     * @param id    the ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Method to get the ID
     * @return  the ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Method to get the Book object from the inventory item
     * @return  the Book object
     */
    public Book getBook() {
        return book;
    }

    /**
     * Method to set the Book object of the inventory item
     * @param book  the Book object
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Method to get the quantity of the inventory item
     * @return  the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Method to set the quantity of the inventory item
     * @param quantity  the quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
