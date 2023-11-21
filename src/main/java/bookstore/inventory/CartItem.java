package bookstore.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Cart Item Object
 * @author Maisha Abdullah
 */

@Entity
public class CartItem extends Item {

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    /**
     * Default constructor
     */
    public CartItem(){

    }

    /**
     * Constructor for CartItem
     * @param book      Book that is the item
     * @param quantity  Quantity of that item
     */
    public CartItem(Book book, int quantity){
        super(book, quantity);
    }


    /**
     * Constructor for CartItem
     * @param book book
     * @param quantity quantity
     * @param shoppingCart shopping cart
     */
    public CartItem (Book book, int quantity, ShoppingCart shoppingCart){
        super(book,quantity);
        this.shoppingCart = shoppingCart;
    }

    /**
     * Getter method for shopping cart
     * @return shopping cart
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Setter method for shopping cart
     * @param shoppingCart shopping cart
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
