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

    /**
     * Setup sample books
     * @param inventoryRepository inventory repo
     * @param bookRepo repo of books
     * @param authorRepo repo of authors
     * @param itemRepository repo of inventory items
     * @return Commandline runner object
     *
     * @author Shrimei Chock
     * @author Maisha Abdullah
     */
    @Bean
    public CommandLineRunner demoInventory(InventoryRepository inventoryRepository, BookRepository bookRepo, AuthorRepository authorRepo, InventoryItemRepository itemRepository,
                                           UserRepository userRepository, ShoppingCartRepository shoppingCartRepository) {
        return (args) -> {
            Book book1, book2, book3, book4;
            InventoryItem item1, item2, item3, item4;
            Inventory inventory = new Inventory();
            inventoryRepository.save(inventory);

            ArrayList<Author> author_list1 = new ArrayList<>();
            Author author1 = new Author("Harper", "Lee");
            author_list1.add(author1);

            String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
            book1 = new Book("0446310786", "To Kill a Mockingbird", author_list1, 12.99, "1960-07-11", "https://media.glamour.com/photos/56e1f3c4bebf143c52613c0b/master/w_1600%2Cc_limit/entertainment-2016-02-next-main.jpg", "Grand Central Publishing", "Classical", description1);
            author1.addBook(book1); //add to bibliography
            authorRepo.save(author1);
            bookRepo.save(book1);
            item1 = new InventoryItem(book1, 5, inventory);
            itemRepository.save(item1);

            ArrayList<Author> author_list2 = new ArrayList<>();
            Author author2 = new Author("Khaled", "Hosseini");
            author_list2.add(author2);

            String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
            book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.00, "2003-05-29", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
            authorRepo.save(author2);
            bookRepo.save(book2);
            item2 = new InventoryItem(book2, 10, inventory);
            itemRepository.save(item2);

            String description3 = "Twenty-six-year-old Jean Louise Finch returns home to Maycomb, Alabama from New York City to visit her aging father, Atticus.";
            book3 = new Book("978-0-06-240985-0", "Go Set a Watchman", author_list1, 14.99, "2015-07-14", "https://m.media-amazon.com/images/I/61lFywIUwzL.jpg", "Harper Collins", "Historical fiction", description3);
            bookRepo.save(book3);
            item3 = new InventoryItem(book3, 2, inventory);
            itemRepository.save(item3);

            ArrayList<Author> author_list3 = new ArrayList<>();
            Author author3 = new Author("Jane", "Austen");
            Author author4 = new Author("Vivien", "Jones");
            author_list3.add(author3);
            author_list3.add(author4);

            String description4 = "When Elizabeth Bennet first meets eligible bachelor Fitzwilliam Darcy, she thinks him arrogant and conceited; he is indifferent to her good looks and lively mind";
            book4 = new Book("9780141439518", "Pride and Prejudice", author_list3, 12.00, "2002-12-13", "https://m.media-amazon.com/images/I/910oVuxR8lS._SL1500_.jpg", "Penguin Classics", "Historical fiction", description4);
            authorRepo.save(author3);
            authorRepo.save(author4);
            bookRepo.save(book4);
            item4 = new InventoryItem(book4, 20, inventory);
            itemRepository.save(item4);

            inventory.addItemToInventory(item1);
            inventory.addItemToInventory(item2);
            inventory.addItemToInventory(item3);
            inventory.addItemToInventory(item4);

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
