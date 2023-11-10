package bookstore.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.stereotype.Controller;

/**
 * Bookstore Owner
 *
 * @author Thanuja Sivaananthan
 */
@Entity
@Controller
public class BookOwner extends BookUser {

    @Id
    @GeneratedValue
    private Long id;

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

    // add other methods here
    public void uploadBookInfo(){
        throw new UnsupportedOperationException("uploadBookInfo not yet supported");
    }
    public void editBookInfo(){
        throw new UnsupportedOperationException("editBookInfo not yet supported");
    }

}
