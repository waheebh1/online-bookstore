/**
 * Shopping cart Object
 * @author Maisha Abdullah
 */

package bookstore.inventory;

import jakarta.persistence.*;


import java.util.ArrayList;

@Entity
public class ShoppingCart {

    //add user
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    private ArrayList<InventoryItem> booksInCart = new ArrayList<>();
    @ManyToOne (fetch = FetchType.EAGER)
    private Inventory inventory;
    private float totalPrice;

    /**
     * Constructor for ShoppingCart
     * @param inventory     the inventory from which the cart shops from
     */
    public ShoppingCart(Inventory inventory){
        this.inventory = inventory;
        this.totalPrice = 0;
    }

    /**
     * Default Constructor
     */
    public ShoppingCart() {
        this.inventory = new Inventory();
        this.totalPrice = 0;
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

        if (quantity > 0){ //quantity must be positive

            for (InventoryItem itemInCart : booksInCart) {
                //item exists in cart, increase the quantity
                if (itemInCart.getBook().equals(book)) {
                    itemInCart.setQuantity(itemInCart.getQuantity() + quantity);
                    bookAdded = true;
                    bookExists = true;
                    break;
                }

                //reduce from inventory
                for (InventoryItem itemInInventory : inventory.getAvailableBooks()){
                    if (itemInInventory.equals(itemInCart)){
                        itemInInventory.setQuantity(itemInInventory.getQuantity() - itemInCart.getQuantity());
                        break;
                    }
                }
            }

            if (!bookExists){
                //does not exist, add new item if it exists in inventory
                for (InventoryItem inventoryItems : inventory.getAvailableBooks()){
                    if (inventoryItems.getBook().equals(book)){
                        booksInCart.add(new InventoryItem(book, quantity));
                        bookAdded = true;
                    }
                }
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

            for (InventoryItem itemInCart : booksInCart) {
                if (itemInCart.getBook().equals(book) && quantity <= itemInCart.getQuantity()) {
                    itemInCart.setQuantity(itemInCart.getQuantity() - quantity);
                    if (itemInCart.getQuantity() == 0){
                        booksInCart.remove(itemInCart);
                    }
                    bookRemoved = true;
                    break;
                }

                //put back into inventory
                for (InventoryItem itemInInventory : inventory.getAvailableBooks()) {
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
        totalPrice = 0;
        //update price
        for (InventoryItem item : booksInCart){
            totalPrice += (float) (item.getBook().getPrice() * item.getQuantity());
        }
    }

    /**
     * Method to get the total price of cart
     * @return  the total price
     */
    public float getTotalPrice() {
        return totalPrice;
    }

    /**
     * Method to get the books in cart
     * @return  books in cart
     */
    public ArrayList<InventoryItem> getBooksInCart() {
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
}
