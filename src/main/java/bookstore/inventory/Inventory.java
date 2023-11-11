/**
 * Inventory Object
 * @author Maisha Abdullah
 */

package bookstore.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inventory {

    @OneToMany(fetch = FetchType.EAGER)
    private List<InventoryItem> availableBooks;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Constructor for Inventory
     * @param availableBooks    the list of all available books in Inventory
     */
    public Inventory(ArrayList<InventoryItem> availableBooks){
        this.availableBooks = availableBooks;
    }

    /**
     * Default constructor
     */
    public Inventory() {
        this.availableBooks = new ArrayList<>();
    }

    /**
     * Method to add books into the inventory
     * @param newItem   Inventory Item to be added
     * @return          returns whether book was successfully added
     */
    public boolean addItemToInventory(InventoryItem newItem){

        boolean bookExists = false;
        boolean bookAddedSuccessfully = false;

        if (newItem.getQuantity() > 0){ //quantity must be positive
            for (InventoryItem existingItem : availableBooks){

                //book exists in the inventory
                if (existingItem.getBook().equals(newItem.getBook())){
                    existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                    bookExists = true;
                    break; // Exit the loop since the book has been found and updated
                }
            }

            //book does not exist
            if (!bookExists) {
                // Book is not in the inventory, add a new InventoryItem
                availableBooks.add(newItem);
            }

            bookAddedSuccessfully = true;
        }
        return bookAddedSuccessfully;
    }

    /**
     * Method to remove books from the inventory
     * @param removeItem    item to be removed
     * @return              returns whether book was successfully removed
     */
    public boolean removeItemFromInventory(InventoryItem removeItem) {

        boolean bookExists = false;
        boolean bookRemovedSuccessfully = false;

        if (removeItem.getQuantity() > 0) { //quantity must be positive
            //if book is in list, can remove
            for (InventoryItem existingItem : availableBooks) {

                //book exists in the inventory
                if (existingItem.getBook().equals(removeItem.getBook()) && removeItem.getQuantity() <= existingItem.getQuantity()) {
                    existingItem.setQuantity(existingItem.getQuantity() - removeItem.getQuantity());
                    if (existingItem.getQuantity() == 0){
                        availableBooks.remove(existingItem);
                    }
                    bookRemovedSuccessfully = true;
                    bookExists = true;
                    break; // Exit the loop since the book has been found and updated
                }
            }

            //book does not exist
            if (!bookExists) {
                return false;
            }
        }

        return bookRemovedSuccessfully;
    }

    /**
     * Method to retrieve the inventory items
     * @return  an arraylist of all inventory items.
     */
    public List<InventoryItem> getAvailableBooks() {
        return availableBooks;
    }

    /**
     * Method to set the ID of the inventory
     * @param id    the ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Method to get the ID of the inventory
     * @return  the ID
     */
    public Long getId() {
        return id;
    }
}
