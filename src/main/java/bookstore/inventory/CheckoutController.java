package bookstore.inventory;

import bookstore.users.BookUser;
import bookstore.users.UserRepository;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository loggedInUserRepository;

    /**
     * Constructor for checkout controller
     * @param authorRepo repository of authors
     * @param bookRepo repository of books
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo,
                                UserRepository loggedInUserRepository){
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.inventoryRepository = inventoryRepo;
        this.inventoryItemRepository = inventoryItemRepo;
        this.loggedInUserRepository = loggedInUserRepository;
    }

    /**
     * List all books in inventory
     * @param model container
     * @return reroute to html page to display all books
     * @author Maisha Abduallah
     * @author Thanuja Sivaananthan
     */
    @GetMapping("/listAvailableBooks")
    public String listAvailableBooks(@RequestParam(name="searchValue", required=false, defaultValue="") String searchValue, Model model) {

        List<BookUser> loggedInUsers = (List<BookUser>) loggedInUserRepository.findAll();
        BookUser loggedInUser = null;
        if (!loggedInUsers.isEmpty()){
            loggedInUser = loggedInUsers.get(0);
        }

        Inventory inventory = inventoryRepository.findById(1); // assuming one inventory

        List<InventoryItem> inventoryItems;
        if (searchValue.isEmpty()){
            inventoryItems = inventory.getAvailableBooks();
        } else {
            inventoryItems = inventory.getBooksMatchingSearch(searchValue);
            if (inventoryItems.isEmpty()){
                model.addAttribute("error", "No items match \"" + searchValue + "\".");
            }
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("inventoryItems", inventoryItems);
        return "home";
    }

    /**
     * View details for a single book
     * @param model container
     * @return route to html page to display contents of a book when clicked
     */
    @GetMapping("/viewBook")
    public String viewBook(@RequestParam(name="isbn") String isbn, Model model) { //TODO pass in isbn when calling this endpoint
        Book bookToDisplay = bookRepository.findByIsbn(isbn);
        model.addAttribute("book", bookToDisplay);
        return "book-info";
    }





}
