package bookstore;


import bookstore.inventory.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

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
            InventoryItem item1;
            InventoryItem item2;

            ArrayList<Author> author_list = new ArrayList<>();
            Author author1 = new Author("Harper", "Lee");
            author_list.add(author1);

            String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
            book1 = new Book("0446310786", "To Kill a Mockingbird", "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", author_list, "Grand Central Publishing", "Classical", 12.99, description1);
            author1.addBook(book1); //add to bibliography
            authorRepo.save(author1);
            bookRepo.save(book1);
            item1 = new InventoryItem(book1, 5);
            itemRepository.save(item1);

            ArrayList<Author> author_list2 = new ArrayList<>();
            Author author2 = new Author("Khaled", "Hosseini");
            author_list2.add(author2);
            String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
            book2 = new Book("1573222453", "The Kite Runner", "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", author_list2,"Riverhead Books", "Historical fiction", 22.00, description2);
            authorRepo.save(author2);
            bookRepo.save(book2);
            item2 = new InventoryItem(book2, 10);
            itemRepository.save(item2);


            Inventory inventory = new Inventory();
            inventory.addItemToInventory(item1);
            inventory.addItemToInventory(item2);

            inventoryRepository.save(inventory);
        };
    }

}
