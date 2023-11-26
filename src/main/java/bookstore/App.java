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
    public CommandLineRunner demoInventory(InventoryRepository inventoryRepository, BookRepository bookRepo, AuthorRepository authorRepo, InventoryItemRepository itemRepository,
                                           UserRepository userRepository, ShoppingCartRepository shoppingCartRepository) {
        return (args) -> {
            Book book1;
            Book book2;
            InventoryItem item1;
            InventoryItem item2;
            Inventory inventory = new Inventory();
            inventoryRepository.save(inventory);

            ArrayList<Author> author_list = new ArrayList<>();
            Author author1 = new Author("Harper", "Lee");
            author_list.add(author1);

            String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
            book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
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



            inventory.addItemToInventory(item1);
            inventory.addItemToInventory(item2);

            inventoryRepository.save(inventory);

            // save a few users and owners
            BookOwner bookOwner = new BookOwner("AdminOwner", "Password123");
            BookUser bookUser1 = new BookUser("User1", "Password45");
            BookUser bookUser2 = new BookUser("User2", "Password67");

            ShoppingCart shoppingCart1 = new ShoppingCart(inventory);
            ShoppingCart shoppingCart2 = new ShoppingCart(inventory);
            ShoppingCart shoppingCart3 = new ShoppingCart(inventory);

            // need to save shoppingCarts first, then set shoppingCart for the user, then save the user
            shoppingCartRepository.save(shoppingCart1);
            shoppingCartRepository.save(shoppingCart2);
            shoppingCartRepository.save(shoppingCart3);

            bookOwner.setShoppingCart(shoppingCart1);
            bookUser1.setShoppingCart(shoppingCart2);
            bookUser2.setShoppingCart(shoppingCart3);

            userRepository.save(bookOwner);
            userRepository.save(bookUser1);
            userRepository.save(bookUser2);

        };
    }
}
