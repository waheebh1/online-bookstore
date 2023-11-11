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
    private ShoppingCart shoppingCart;

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
        this.shoppingCart = new ShoppingCart(inventoryRepository.findById(1));
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

    @GetMapping("/addToCart")
    public String addToCartForm(Model model){
        model.addAttribute("inventory", inventoryItemRepository.findAll());
        return "home";
    }
    @PostMapping("/addToCart")
    public String addToCart(@ModelAttribute InventoryItem inventoryItem, Model model){
//        ShoppingCart shoppingCart = new ShoppingCart(inventoryRepository.findById(1));
//        Book book = bookRepository.findByIsbn("0446310786");
//        System.out.println(shoppingCart.addToCart(book, 1));
//        System.out.println(inventoryItemRepository.);

        //TODO - if user does not already have a shopping cart
        //ShoppingCart shoppingCart = new ShoppingCart(inventoryRepository.findById(1));

        //shoppingCartRepository.save(shoppingCart);

        //TODO - change template to allow user to select a book
        InventoryItem invItem = inventoryItemRepository.findById(1);
        shoppingCart.addToCart(invItem.getBook(), 1);
        System.out.println(invItem.getQuantity());
        System.out.println(shoppingCart.getBooksInCart());

        inventoryItemRepository.save(invItem);
        //TODO - saving the shopping cart does not work (Inventory Item is not saved?)
        //shoppingCartRepository.save(shoppingCart);
        //inventoryRepository.save(inventoryRepository.findById(1));

        model.addAttribute("inventory",inventoryItemRepository.findAll());
        return "home";
    }
}
