/**
 * Inventory Object
 */

package bookstore.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inventory {

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.MERGE)
    private List<InventoryItem> availableBooks;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Constructor for Inventory
     * @param availableBooks    the list of all available books in Inventory
     * @author Maisha Abdullah
     */
    public Inventory(ArrayList<InventoryItem> availableBooks){
        this.availableBooks = availableBooks;
    }

    /**
     * Default constructor
     * @author Maisha Abdullah
     */
    public Inventory() {
        this.availableBooks = new ArrayList<>();
    }

    /**
     * Method to add books into the inventory
     * @param newItem Inventory Item to be added
     * @return returns whether book was successfully added
     * @author Maisha Abdullah
     */
    public boolean addItemToInventory(InventoryItem newItem){

        if (newItem.getQuantity() > 0){ //quantity must be positive
            for (InventoryItem existingItem : availableBooks){

                //book exists in the inventory
                if (existingItem.getBook().equals(newItem.getBook())){
                    existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                    return true;
                }
            }

            // Book does not exist
            // Book is not in the inventory, add a new InventoryItem
            availableBooks.add(newItem);

            return true;
        }
        return false;
    }

    /**
     * Method to take an item out of the inventory with adjusted quantity
     * @param book the book
     * @param quantity the quantity
     * @return if book was removed successfully
     * @author Maisha Abdullah
     */
    public boolean reduceFromInventory(Book book, int quantity){

        if (quantity > 0) { //quantity must be positive
            for (InventoryItem existingItem : availableBooks) {

                //book exists in the inventory
                if (existingItem.getBook().getIsbn().equals(book.getIsbn()) && quantity <= existingItem.getQuantity()) {
                    existingItem.setQuantity(existingItem.getQuantity() - quantity);
                    if (existingItem.getQuantity() == 0){
                        availableBooks.remove(existingItem);
                    }
                    return true;
                }
            }

            //book does not exist
            return false;
        }
        return false;
    }

    /**
     * Method to put an item back into inventory with adjusted quantity
     * @param book the book
     * @param quantity the quantity
     * @return if book was added successfully
     * @author Maisha Abdullah
     */
    public boolean putBackIntoInventory(Book book, int quantity){

        if (quantity > 0) { //quantity must be positive
            for (InventoryItem existingItem : availableBooks) {

                //book exists in the inventory
                if (existingItem.getBook().getIsbn().equals(book.getIsbn())) {
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                    return true;
                }
            }
            //book does not exist
            return false;
        }

        return false;
    }


    /**
     * Method to find an available book in Inventory by checking the isbn
     * @param isbn the isbn
     * @return the inventory item
     * @author Maisha Abdullah
     */
    public InventoryItem findAvailableBook(String isbn){
        for (InventoryItem existingItem : availableBooks) {
            if (existingItem.getBook().getIsbn().equals(isbn)){
                return existingItem;
            }
        }
        return null;
    }

    /**
     * Method to retrieve the inventory items
     * @return  an arraylist of all inventory items.
     * @author Maisha Abdullah
     */
    public List<InventoryItem> getAvailableBooks() {
        return availableBooks;
    }


    /**
     * Search for specific inventory items
     * @param searchValue   value to search with
     * @return              list of matching inventory items
     * @author Thanuja Sivaananthan
     */
    public List<InventoryItem> getBooksMatchingSearch(String searchValue) {

        if (searchValue.isEmpty()){
            return availableBooks;
        }

        List<InventoryItem> searchedBooks = new ArrayList<>();
        searchValue = searchValue.toLowerCase();

        // most related search items
        for (InventoryItem inventoryItem : availableBooks){
            Book book = inventoryItem.getBook();
            // check title, genre, publisher
            if (book.getTitle().toLowerCase().contains(searchValue)
                    || book.getGenre().toLowerCase().contains(searchValue)
                    || book.getPublisher().toLowerCase().contains(searchValue)){
                searchedBooks.add(inventoryItem);
            } else {
                // check authors
                for (Author author : book.getAuthor()){
                    if (author.getFullName().toLowerCase().contains(searchValue)){
                        searchedBooks.add(inventoryItem);
                    }
                }

            }
        }

        // less related search items
        for (InventoryItem inventoryItem : availableBooks){
            if (!searchedBooks.contains(inventoryItem)) {
                Book book = inventoryItem.getBook();
                // check description
                if (book.getDescription().toLowerCase().contains(searchValue)) {
                    searchedBooks.add(inventoryItem);
                }
            }
        }

        return searchedBooks;
    }

    /**
     * Method to set the ID of the inventory
     * @param id the ID
     * @author Maisha Abdullah
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Method to get the ID of the inventory
     * @return the ID
     * @author Maisha Abdullah
     */
    public Long getId() {
        return id;
    }
}
