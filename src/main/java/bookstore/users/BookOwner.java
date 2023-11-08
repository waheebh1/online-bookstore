package bookstore.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.stereotype.Controller;

@Entity
@Controller
public class BookOwner extends BookUser {

    @Id
    @GeneratedValue
    private Long id;

    public BookOwner(Long id, String username, String password) {
        super(id, username, password);
    }

    public BookOwner(String username, String password) {
        super(username, password);
    }

    public BookOwner() {
        super(0L, "", "");
    }

    // add other methods here
    public void uploadBookInfo(){}
    public void editBookInfo(){}

}
