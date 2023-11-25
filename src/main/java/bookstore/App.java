package bookstore;

import bookstore.inventory.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import bookstore.users.BookOwner;
import bookstore.users.BookUser;
import bookstore.users.UserRepository;

@SpringBootApplication
public class App
{
    public static void main(String[] args) {SpringApplication.run(App.class);
    }

    @Bean
    public CommandLineRunner demoInventory(InventoryRepository inventoryRepository, BookRepository bookRepo, AuthorRepository authorRepo, InventoryItemRepository itemRepository) {
        return (args) -> {
            Book book1;
            Book book2;
            Book book3;
            InventoryItem item1;
            InventoryItem item2;
            InventoryItem item3;
            Inventory inventory = new Inventory();
            inventoryRepository.save(inventory);

            ArrayList<Author> author_list1 = new ArrayList<>();
            Author author1 = new Author("Harper", "Lee");
            author_list1.add(author1);

            String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
            book1 = new Book("0446310786", "To Kill a Mockingbird", author_list1, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
            author1.addBook(book1); //add to bibliography
            authorRepo.save(author1);
            bookRepo.save(book1);
            item1 = new InventoryItem(book1, 5, inventory);
            itemRepository.save(item1);

            ArrayList<Author> author_list2 = new ArrayList<>();
            Author author2 = new Author("Khaled", "Hosseini");
            author_list2.add(author2);

            String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
            book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.00, "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
            authorRepo.save(author2);
            bookRepo.save(book2);
            item2 = new InventoryItem(book2, 10, inventory);
            itemRepository.save(item2);

            String description3 = "Twenty-six-year-old Jean Louise Finch returns home to Maycomb, Alabama from New York City to visit her aging father, Atticus.";
            book3 = new Book("978-0-06-240985-0", "Go Set a Watchman", author_list1, 14.99, "14/07/2015", "https://m.media-amazon.com/images/I/61lFywIUwzL.jpg", "Harper Collins", "Historical fiction", description3);
            bookRepo.save(book3);
            item3 = new InventoryItem(book3, 2, inventory);
            itemRepository.save(item3);

            inventory.addItemToInventory(item1);
            inventory.addItemToInventory(item2);
            inventory.addItemToInventory(item3);

            inventoryRepository.save(inventory);
        };
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
