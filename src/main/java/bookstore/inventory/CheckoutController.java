package bookstore.inventory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Constructor for checkout controller
     * @param authorRepo repository of authors
     * @param bookRepo repository of books
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, ShoppingCartRepository cartRepo, InventoryRepository inventoryRepo){
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.shoppingCartRepository = cartRepo;
        this.inventoryRepository = inventoryRepo;
    }

    /**
     * List all books in inventory
     * @param model container
     * @return reroute to html page to display all books
     */
    @GetMapping("/books")
    public String listAvailableBooks(Model model) {
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);

        String description = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        Book book1 = new Book("0446310786", "To Kill a Mockingbird", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", author_list, "Grand Central Publishing", "Classical", 12.99, description);
        author1.addBook(book1); //add to bibliography
        authorRepository.save(author1);
        bookRepository.save(book1);

        List<Book> books = (List<Book>) bookRepository.findAll();
        System.out.println(books);
        model.addAttribute("books", books);
        return "home";
    }

}
