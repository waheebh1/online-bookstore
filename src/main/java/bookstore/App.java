package bookstore;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import bookstore.users.BookOwner;
import bookstore.users.BookUser;
import bookstore.users.UserRepository;

@SpringBootApplication
public class App
{
    public static void main(String[] args) {SpringApplication.run(App.class);
    }

    /**
     * Setup sample users
     * @author Thanuja Sivaananthan
     *
     * @param repository    user repository
     * @return              CommandLineRunner object
     */
    @Bean
    public CommandLineRunner demoUsers(UserRepository repository) {
        return (args) -> {
            // save a few users and owners
            repository.save(new BookOwner("AdminOwner", "Password123"));
            repository.save(new BookUser("User1", "Password45"));
            repository.save(new BookUser("User2", "Password67"));
        };
    }

}
