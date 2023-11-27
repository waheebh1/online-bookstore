package bookstore.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;


/**
 * Usersession
 *
 * @author Thanuja Sivaananthan
 */
@Entity
public class Usersession {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private BookUser bookUser;

    public Usersession(BookUser bookUser){
        this.bookUser = bookUser;
    }

    public Usersession(){
        this(null);
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


    public BookUser getBookUser() {
        return bookUser;
    }

    public void setBookUser(BookUser bookUser) {
        this.bookUser = bookUser;
    }
}
