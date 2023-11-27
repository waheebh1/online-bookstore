package bookstore.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Cart Item Object
 * @author Maisha Abdullah
 */

@Entity
public class ShoppingCartItem extends Item {

    @ManyToOne
    @JoinColumn(name = "shopping_cart_id")
    private ShoppingCart shoppingCart;

    /**
     * Default constructor
     * @author maisha abdullah
     */
    public ShoppingCartItem(){

    }

    /**
     * Constructor for ShoppingCartItem
     * @param book      Book that is the item
     * @param quantity  Quantity of that item
     * @author maisha abdullah
     */
    public ShoppingCartItem(Book book, int quantity){
        super(book, quantity);
    }


    /**
     * Constructor for ShoppingCartItem
     * @param book book
     * @param quantity quantity
     * @param shoppingCart shopping cart
     * @author maisha abdullah
     */
    public ShoppingCartItem(Book book, int quantity, ShoppingCart shoppingCart){
        super(book,quantity);
        this.shoppingCart = shoppingCart;
    }

    /**
     * Getter method for shopping cart
     * @return shopping cart
     * @author maisha abdullah
     */
    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    /**
     * Setter method for shopping cart
     * @param shoppingCart shopping cart
     * @author maisha abdullah
     */
    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }
}
