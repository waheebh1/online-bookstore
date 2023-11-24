/**
 * Shopping cart Object
 * @author Maisha Abdullah
 */

package bookstore.inventory;

import bookstore.users.BookUser;
import jakarta.persistence.*;


import java.util.ArrayList;
import java.util.List;

@Entity
public class ShoppingCart {

    @OneToOne
    private BookUser user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL)
    private List<ShoppingCartItem> booksInCart = new ArrayList<>();
    @OneToOne
    private Inventory inventory;
    private Double totalPrice;

    /**
     * Constructor for ShoppingCart
     * @param inventory     the inventory from which the cart shops from
     * @author Maisha Abdullah
     */
    public ShoppingCart(Inventory inventory){
        this.inventory = inventory;
        this.totalPrice = 0.0;
    }

    /**
     * Constructor for ShoppingCart
     * @param inventory     the inventory from which the cart shops from
     * @author Maisha Abdullah
     */
    public ShoppingCart(Inventory inventory, BookUser user){
        this.inventory = inventory;
        this.totalPrice = 0.0;
        this.user = user;
    }

    /**
     * Default Constructor
     * @author Maisha Abdullah
     */
    public ShoppingCart() {
        this.inventory = new Inventory();
        this.totalPrice = 0.0;
    }

    /**
     * Method to add to cart
     * @param book the Book user wishes to add to cart
     * @param quantity the quantity of books
     * @return returns if book was added to cart
     * @author Maisha Abdullah
     * @author Thanuja Sivaananthan
     */
    public boolean addToCart(Book book, int quantity){
        InventoryItem inventoryItem = inventory.findAvailableBook(book.getIsbn());

        // Handle invalid quantity
        if (quantity <= 0) {
            return false;
        }

        // Add only if the book is still in inventory
        if (inventoryItem != null && inventoryItem.getQuantity() >= quantity) {

            boolean bookExistsInCart = false;

            for (ShoppingCartItem cartItem : booksInCart) {
                // If the item exists in cart, increase the quantity
                if (cartItem.getBook().getIsbn().equals(book.getIsbn()) && inventoryItem.getQuantity() >= quantity) {
                    cartItem.setQuantity(cartItem.getQuantity() + quantity);

                    // Reduce quantity from the inventory
                    inventory.reduceFromInventory(book, quantity);

                    bookExistsInCart = true;
                }
            }

            if (!bookExistsInCart){
                // Book is available in the inventory
                ShoppingCartItem newCartItem = new ShoppingCartItem(book, quantity, this);
                booksInCart.add(newCartItem);

                // Reduce quantity from the inventory
                inventory.reduceFromInventory(book, quantity);
            }

            updateTotalPrice();
            return true;
        }

        return false;
    }


    /**
     * Method to remove from cart
     * @param book      the Book user wishes to remove from cart
     * @param quantity  the quantity of books
     * @return          returns if book was added to cart
     * @author Maisha Abdullah
     * @author Thanuja Sivaananthan
     */
    public boolean removeFromCart(Book book, int quantity){
        InventoryItem inventoryItem = inventory.findAvailableBook(book.getIsbn());

        // Handle invalid quantity
        if (quantity <= 0) {
            return false;
        }

        for (ShoppingCartItem cartItem : booksInCart) {
            // Item exists in cart, increase the quantity
            if (cartItem.getBook().getIsbn().equals(book.getIsbn()) && cartItem.getQuantity() >= quantity) {
                int newQuantity = cartItem.getQuantity() - quantity;

                // this always needs to happen for some reason?
                cartItem.setQuantity(cartItem.getQuantity() - quantity);

                if (newQuantity <= 0) {
                    booksInCart.remove(cartItem);
                }

                // Reduce quantity from the inventory
                inventory.putBackIntoInventory(book, quantity);
                updateTotalPrice();
                return true;
            }
        }

        updateTotalPrice();
        return false;
    }

    /**
     * Method to update total price
     * @author Maisha Abdullah
     * @author Shrimei Chock
     * @author Thanuja Sivaananthan
     */
    private void updateTotalPrice(){
        totalPrice = 0.0;
        //update price
        for (ShoppingCartItem item : booksInCart){
            totalPrice += (item.getBook().getPrice() * item.getQuantity());
        }
    }

    /**
     * Method to get the total price of cart
     * @return  the total price
     * @author Maisha Abdullah
     * @author Shrimei Chock
     */
    public Double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Method to get the books in cart
     * @return  books in cart
     * @author Maisha Abdullah
     */
    public List<ShoppingCartItem> getBooksInCart() {
        return booksInCart;
    }

    /**
     * Method to clear cart once checkout is completed?
     * @author Maisha Abdullah
     */
    //im confusion?
    public void checkout(){
        booksInCart.clear();
    }

    /**
     * Method to get the inventory of shopping cart
     * @return  the inventory
     * @author Maisha Abdullah
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Method to set the inventory of shopping cart
     * @param inventory the inventory
     * @author Maisha Abdullah
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Method to set the ID of the shopping cart
     * @param id the ID
     * @author Maisha Abdullah
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Method to get the ID of the shopping cart
     * @return the ID
     * @author Maisha Abdullah
     */
    public Long getId() {
        return id;
    }

    /**
     * Method to get the user of this shopping cart
     * @return the user
     * @author Maisha Abdullah
     */
    public BookUser getUser() {
        return user;
    }

    /**
     * Method to set the user of this shopping cart
     * @param user the user
     * @author Maisha Abdullah
     */
    public void setUser(BookUser user) {
        this.user = user;
    }

    /**
     * Method to get the total quantity of the shopping cart
     * @return the total quantity
     * @author Maisha Abdullah
     */
    public int getTotalQuantityOfCart(){
        int total = 0;
        for (ShoppingCartItem itemInCart : booksInCart) {
            total += itemInCart.getQuantity();
        }
        return total;
    }
}
