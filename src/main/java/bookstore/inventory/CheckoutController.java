package bookstore.inventory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    /**
     * Constructor for checkout controller
     * @param authorRepo repository of authors
     * @param bookRepo repository of books
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo){
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.inventoryRepository = inventoryRepo;
        this.inventoryItemRepository = inventoryItemRepo;
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

        Inventory inventory = inventoryRepository.findById(1); // assuming one inventory

        Iterable<InventoryItem> inventoryItems;
        if (searchValue.isEmpty()){
            inventoryItems = inventory.getAvailableBooks();
        } else {
            System.out.println("SEARCH FOR " + searchValue);
            inventoryItems = inventory.getBooksMatchingSearch(searchValue);
        }

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
