package bookstore.users;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.stereotype.Controller;

@Entity
@Controller
public class BookUser {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    public BookUser(Long id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public BookUser(String username, String password){
        this.username = username;
        this.password = password;
    }

    public BookUser() {
        this(0L, "", "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // add other methods here

}
