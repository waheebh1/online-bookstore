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
    private List<CartItem> booksInCart = new ArrayList<>();
    @OneToOne
    private Inventory inventory;
    private Double totalPrice;

    /**
     * Constructor for ShoppingCart
     * @param inventory     the inventory from which the cart shops from
     */
    public ShoppingCart(Inventory inventory){
        this.inventory = inventory;
        this.totalPrice = 0.0;
    }

    public ShoppingCart(Inventory inventory, BookUser user){
        this.inventory = inventory;
        this.totalPrice = 0.0;
        this.user = user;
    }

    /**
     * Default Constructor
     */
    public ShoppingCart() {
        this.inventory = new Inventory();
        this.totalPrice = 0.0;
    }

    /**
     * Method to add to cart
     * @param book      the Book user wishes to add to cart
     * @param quantity  the quantity of books
     * @return          returns if book was added to cart
     */
    public boolean addToCart(Book book, int quantity){
        //this.totalPrice = 0;

        boolean bookExists = false;
        boolean bookAdded = false;
        CartItem itemAdded = null;
        InventoryItem inventoryItem = inventory.findAvailableBook(book.getIsbn());

        if (quantity <= 0) {
            // Handle invalid quantity
            return false;
        }

        for (CartItem itemInCart : booksInCart) {
            //item exists in cart, increase the quantity
            if (itemInCart.getBook().getIsbn().equals(book.getIsbn()) && inventoryItem.getQuantity() >= quantity) {
                itemInCart.setQuantity(itemInCart.getQuantity() + quantity);

                // Reduce quantity from the inventory
                inventory.reduceFromInventory(book, quantity);
                bookAdded = true;
                bookExists = true;
                itemAdded = itemInCart;
                break;
            }
        }

        if (itemAdded == null) {
            // Book does not exist in the cart, check if it's in the inventory
            if (inventoryItem != null && inventoryItem.getQuantity() >= quantity) {
                // Book is available in the inventory
                CartItem newItem = new CartItem(book, quantity, this);
                booksInCart.add(newItem);
                itemAdded = newItem;

                // Reduce quantity from the inventory
                inventory.reduceFromInventory(book, quantity);

                bookAdded = true;
            }
        }

        updateTotalPrice();
        return bookAdded;
    }


    /**
     * Method to remove from cart
     * @param book      the Book user wishes to remove from cart
     * @param quantity  the quantity of books
     * @return          returns if book was added to cart
     */
    public boolean removeFromCart(Book book, int quantity){

        boolean bookRemoved = false;

        if (quantity > 0) { //quantity must be positive

            for (CartItem itemInCart : booksInCart) {
                if (itemInCart.getBook().equals(book) && quantity <= itemInCart.getQuantity()) {
                    itemInCart.setQuantity(itemInCart.getQuantity() - quantity);
                    if (itemInCart.getQuantity() == 0){
                        booksInCart.remove(itemInCart);
                    }
                    bookRemoved = true;
                    break;
                }

                //put back into inventory
                for (Item itemInInventory : inventory.getAvailableBooks()) {
                    if (itemInInventory.equals(itemInCart)) {
                        itemInInventory.setQuantity(itemInInventory.getQuantity() + itemInCart.getQuantity());
                        break;
                    }
                }
            }
        }
        updateTotalPrice();

        return bookRemoved;
    }

    /**
     * Method to update total price
     */
    private void updateTotalPrice(){
        totalPrice = 0.0;
        //update price
        for (CartItem item : booksInCart){
            totalPrice += (float) (item.getBook().getPrice() * item.getQuantity());
        }
    }

    /**
     * Method to get the total price of cart
     * @return  the total price
     */
    public Double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Method to get the books in cart
     * @return  books in cart
     */
    public List<CartItem> getBooksInCart() {
        return booksInCart;
    }

    /**
     * Method to clear cart once checkout is completed?
     */
    //im confusion?
    public void checkout(){
        booksInCart.clear();
    }

    /**
     * Method to get the inventory of shopping cart
     * @return  the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Method to set the inventory of shopping cart
     * @param inventory the inventory
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public BookUser getUser() {
        return user;
    }

    public void setUser(BookUser user) {
        this.user = user;
    }

    public int getTotalQuantityOfCart(){
        int total = 0;
        for (CartItem itemInCart : booksInCart) {
            total += itemInCart.getQuantity();
        }
        return total;
    }
}
