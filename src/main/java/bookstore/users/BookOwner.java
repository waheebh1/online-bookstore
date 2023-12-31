package bookstore.users;

import bookstore.inventory.ShoppingCart;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Controller;

/**
 * Bookstore Owner
 *
 * @author Thanuja Sivaananthan
 */
@Entity
public class BookOwner extends BookUser {

    /**
     * Create bookstore owner
     * @author Thanuja Sivaananthan
     *
     * @param id            id of owner
     * @param username      username of owner
     * @param password      password of owner
     */
    public BookOwner(Long id, String username, String password) {
        super(id, username, password);
        this.userType = UserType.BOOKOWNER;
    }

    public BookOwner(Long id, String username, String password, ShoppingCart shoppingCart) {
        super(id, username, password, shoppingCart);
        this.userType = UserType.BOOKOWNER;
    }

    /**
     * Create bookstore owner
     * @author Thanuja Sivaananthan
     *
     * @param username      username of owner
     * @param password      password of owner
     */
    public BookOwner(String username, String password) {
        this(null, username, password);
    }

    /**
     * Create an empty bookstore owner
     * @author Thanuja Sivaananthan
     */
    public BookOwner() {
        this(0L, "", "");
    }

}
