package bookstore.users;

import bookstore.inventory.ShoppingCart;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.springframework.stereotype.Controller;

/**
 * Bookstore User
 *
 * @author Thanuja Sivaananthan
 */
@Entity
public class BookUser {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    public UserType userType;

    @OneToOne
    private ShoppingCart shoppingCart;

    /**
     * Create bookstore user
     * @author Thanuja Sivaananthan
     *
     * @param id            id of owner
     * @param username      username of owner
     * @param password      password of owner
     */
    public BookUser(Long id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = UserType.BOOKUSER;
    }

    public BookUser(Long id, String username, String password, ShoppingCart shoppingCart){
        this.id = id;
        this.username = username;
        this.password = password;
        this.userType = UserType.BOOKUSER;
        this.shoppingCart = shoppingCart;
    }

    /**
     * Create bookstore user
     * @author Thanuja Sivaananthan
     *
     * @param username      username of owner
     * @param password      password of owner
     */
    public BookUser(String username, String password){
        this(null, username, password);
    }

    /**
     * Create an empty bookstore user
     * @author Thanuja Sivaananthan
     */
    public BookUser() {
        this(0L, "", "");
    }

    /**
     * Get id
     * @author Thanuja Sivaananthan
     * @return  id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id
     * @author Thanuja Sivaananthan
     * @param id      id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get username
     * @author Thanuja Sivaananthan
     * @return  username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     * @author Thanuja Sivaananthan
     * @param username      username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get password
     * @author Thanuja Sivaananthan
     * @return  password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set password
     * @author Thanuja Sivaananthan
     * @param password      password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get user type
     * @author Thanuja Sivaananthan
     * @return user type
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Set user type
     * @author Thanuja Sivaananthan
     * @param userType user type
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Set user type
     * @author Thanuja Sivaananthan
     * @param userTypeString user type
     */
    public void setUserType(String userTypeString) {
        this.userType = UserType.valueOf(userTypeString);
    }

    public void setShoppingCart(ShoppingCart shoppingCart) {
        this.shoppingCart = shoppingCart;
    }

    public ShoppingCart getShoppingCart() {
        return shoppingCart;
    }

    // add other methods here

}
