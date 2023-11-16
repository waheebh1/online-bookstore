package bookstore.inventory;

import bookstore.users.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryItemRepository itemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Constructor for checkout controller
     * @param authorRepo repository of authors
     * @param bookRepo repository of books
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo, ShoppingCartRepository shoppingCartRepository, CartItemRepository cartItemRepository){
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.inventoryRepository = inventoryRepo;
        this.itemRepository = inventoryItemRepo;
        this.shoppingCartRepository = shoppingCartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * List all books in inventory
     * @param model container
     * @return reroute to html page to display all books
     */
    @GetMapping("/listAvailableBooks")
    public String listAvailableBooks(Model model) {
        Iterable<InventoryItem> inventory = itemRepository.findAll();
        model.addAttribute("inventory", inventory);
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

    @GetMapping("/addToCart")
    public String addToCartForm(Model model){
        model.addAttribute("inventory", itemRepository.findAll());
        return "home";
    }
    @PostMapping("/addToCart")
    public String addToCart(@RequestParam(name = "selectedItems", required = false) String[] selectedItems, Model model){

        /*
        if (selectedItems != null) {
            for (String selectedItem : selectedItems) {
                System.out.println("Selected Item: " + selectedItem);
                // Perform logic based on selected items
            }
        }
         */

        //TODO - if user does not already have a shopping cart
        //TODO - allow multiple carts (currently 1 total)
        ShoppingCart shoppingCart;
        shoppingCart = shoppingCartRepository.findById(1);
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart(inventoryRepository.findById(1));
        }

        //TODO - change template to allow user to select a book
        InventoryItem invItem = itemRepository.findById(1);
        CartItem cartItem = shoppingCart.addToCart(invItem.getBook(), 1);

        shoppingCartRepository.save(shoppingCart);
        cartItemRepository.saveAll(shoppingCart.getBooksInCart());

        System.out.println(invItem.getQuantity());
        System.out.println(shoppingCart.getBooksInCart());

        itemRepository.save(invItem);

        //TODO - saving the shopping cart does not work (Inventory Item is not saved?)
        inventoryRepository.save(inventoryRepository.findById(1));

        model.addAttribute("inventory", itemRepository.findAll());
        return "home";
    }




}
