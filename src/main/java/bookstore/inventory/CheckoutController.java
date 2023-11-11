package bookstore.inventory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    /**
     * Constructor for checkout controller
     * @param authorRepo repository of authors
     * @param bookRepo repository of books
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, ShoppingCartRepository cartRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo){
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.shoppingCartRepository = cartRepo;
        this.inventoryRepository = inventoryRepo;
        this.inventoryItemRepository = inventoryItemRepo;
    }

    /**
     * List all books in inventory
     * @param model container
     * @return reroute to html page to display all books
     */
    @GetMapping("/listAvailableBooks")
    public String listAvailableBooks(Model model) {
        Iterable<InventoryItem> inventory = inventoryItemRepository.findAll();
        model.addAttribute("inventory", inventory);
        return "home";
    }

}
