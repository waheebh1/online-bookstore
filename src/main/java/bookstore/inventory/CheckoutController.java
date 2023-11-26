package bookstore.inventory;

import bookstore.users.BookUser;
import bookstore.users.UserRepository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository loggedInUserRepository;

    /**
     * Constructor for checkout controller
     *
     * @param authorRepo repository of authors
     * @param bookRepo   repository of books
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo, ShoppingCartRepository shoppingCartRepository, ShoppingCartItemRepository shoppingCartItemRepository, UserRepository loggedInUserRepository) {
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.inventoryRepository = inventoryRepo;
        this.inventoryItemRepository = inventoryItemRepo;
        this.loggedInUserRepository = loggedInUserRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
    }

    /**
     * List all books in inventory
     *
     * @param model container
     * @return reroute to html page to display all books
     * @author Maisha Abdullah
     * @author Thanuja Sivaananthan
     */
    @GetMapping("/listAvailableBooks")
    public String listAvailableBooks
    (@RequestParam(name = "searchValue", required = false, defaultValue = "") String searchValue, @RequestParam(name = "sort", required = false, defaultValue = "low_to_high") String sort, Model model) {
        List<BookUser> loggedInUsers = (List<BookUser>) loggedInUserRepository.findAll();
        BookUser loggedInUser = null;
        if (!loggedInUsers.isEmpty()) {
            loggedInUser = loggedInUsers.get(0);
        }

        Inventory inventory = inventoryRepository.findById(1); // assuming one inventory

        //Search
        List<InventoryItem> inventoryItems;
        if (searchValue.isEmpty()) {
            inventoryItems = inventory.getAvailableBooks();
        } else {
            inventoryItems = inventory.getBooksMatchingSearch(searchValue);
            if (inventoryItems.isEmpty()) {
                model.addAttribute("error", "No items match \"" + searchValue + "\".");
            }
        }

        // Sort after searching
        System.out.println("SORT BY: " + sort);

        if (sort.equals(SortCriteria.LOW_TO_HIGH.label)) {
            inventoryItems.sort(Comparator.comparing(item -> item.getBook().getPrice())); //TODO replace with methods in inventoryItem repo?
        } else if (sort.equals(SortCriteria.HIGH_TO_LOW.label)) {
            inventoryItems.sort(Comparator.comparing(item -> item.getBook().getPrice(), Comparator.reverseOrder()));
        } else {
            inventoryItems.sort(Comparator.comparing(item -> item.getBook().getTitle()));
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("sort", sort);
        return "home";
    }

    /**
     * View details for a single book
     *
     * @param model container
     * @return route to html page to display contents of a book when clicked
     * @author Shrimei Chock
     */
    @GetMapping("/viewBook")
    public String viewBook(@RequestParam(name = "isbn") String isbn, Model model) { //TODO pass in isbn when calling this endpoint
        Book bookToDisplay = bookRepository.findByIsbn(isbn);
        model.addAttribute("book", bookToDisplay);
        return "book-info";
    }

//    @GetMapping("/sort")
//    public String sortBooks(@RequestParam(name = "sort") String sort, Model model){
//        //Debug statement
//        System.out.println("SORT BY: " + sort);
//
//        //Get logged in user
//        List<BookUser> loggedInUsers = (List<BookUser>) loggedInUserRepository.findAll();
//        BookUser loggedInUser = null;
//        if (!loggedInUsers.isEmpty()) {
//            loggedInUser = loggedInUsers.get(0);
//        }
//
//        //Get logged in user's cart
//        ShoppingCart shoppingCart = getOrCreateShoppingCart();
//
//        //Adjust inventory displayed depending on sort criteria
//        Iterable<bookstore.inventory.InventoryItem> inventoryItems;
//
//        if(sort.equals("low_to_high")){ //TODO use enum instead of hardcoding
//            inventoryItems = inventoryItemRepository.sortByPriceAsc();
//        } else if (sort.equals("high_to_low")) {
//            inventoryItems = inventoryItemRepository.sortByPriceDesc();
//        } else {
//            inventoryItems = inventoryItemRepository.sortByTitleAsc();
//        }
//        model.addAttribute("inventoryItems", inventoryItems);
//        model.addAttribute("user", loggedInUser);
//        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
//        model.addAttribute("sort", sort);
//
//        return "home";
//    }

    /**
     * Method to go to an add to cart form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @GetMapping("/addToCart")
    public String addToCartForm(Model model) {
        model.addAttribute("inventory", inventoryItemRepository.findAll());
        //model.addAttribute("user", loggedInUser);
        return "home";
    }

    /**
     * Method to submit an add to cart form
     * @param selectedItems the items selected from the checklist in the form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @PostMapping("/addToCart")
    public String addToCart(@RequestParam(name = "selectedItems", required = false) String[] selectedItems, Model
            model) {

        System.out.println("going into add to cart");
        System.out.println("SELECTED ITEM: " + Arrays.toString(selectedItems));

        List<BookUser> loggedInUsers = (List<BookUser>) loggedInUserRepository.findAll();
        BookUser loggedInUser = null;
        if (!loggedInUsers.isEmpty()) {
            loggedInUser = loggedInUsers.get(0);
        }

        //TODO - if user does not already have a shopping cart
        ShoppingCart shoppingCart = getOrCreateShoppingCart();

        if (selectedItems != null) {
            for (String selectedItem : selectedItems) {

                InventoryItem invItem = inventoryItemRepository.findById(Integer.parseInt(selectedItem));
                System.out.println("INVENTORY ITEM QUANTITY --BEFORE-- ADD TO CART: " + invItem.getQuantity());
                shoppingCart.addToCart(invItem.getBook(), 1);
                System.out.println("INVENTORY ITEM QUANTITY --AFTER-- ADD TO CART: " + invItem.getQuantity());

                shoppingCartRepository.save(shoppingCart);
                shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart());

                inventoryItemRepository.save(invItem);

                System.out.println("CART ITEM FOR BOOK 1 QUANTITY:" + shoppingCart.getBooksInCart().get(0).getQuantity());
                if (shoppingCart.getBooksInCart().size() > 1) {
                    System.out.println("CART ITEM FOR BOOK 2 QUANTITY:" + shoppingCart.getBooksInCart().get(1).getQuantity());
                }

                System.out.println(" TOTAL IN CART: " + shoppingCart.getTotalQuantityOfCart());


                inventoryRepository.save(inventoryRepository.findById(1));

            }
        }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
        model.addAttribute("inventoryItems", inventoryItemRepository.findAll());
        return "home"; //TODO after add/remove from cart, the sort goes away. Need to store the sort value

    }

    /**
     * Method to get the total in cart and update it on the html
     * @return the total in the cart
     * @author Maisha Abdullah
     */
    @GetMapping("/getTotalInCart")
    @ResponseBody
    public int getTotalInCart() {
        System.out.println("going into get total in cart");

        ShoppingCart shoppingCart = getOrCreateShoppingCart();

        return shoppingCart.getTotalQuantityOfCart();
    }

    /**
     * Method to go to remove from cart form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @GetMapping("/removeFromCart")
    public String removeFromCartForm (Model model){
        model.addAttribute("inventory", inventoryItemRepository.findAll());
        return "home";
    }

    /**
     * Method to submit a remove from cart form
     * @param selectedItems the items selected from the checklist in the form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @PostMapping("/removeFromCart")
    public String removeFromCart (@RequestParam(name = "selectedItems", required = false) String[]selectedItems, Model
            model){

        System.out.println("going into remove from cart");
        System.out.println("SELECTED ITEM: " + Arrays.toString(selectedItems));

        List<BookUser> loggedInUsers = (List<BookUser>) loggedInUserRepository.findAll();
        BookUser loggedInUser = null;
        if (!loggedInUsers.isEmpty()) {
            loggedInUser = loggedInUsers.get(0);
        }

        //TODO - if user does not already have a shopping cart
        ShoppingCart shoppingCart = getOrCreateShoppingCart();

        if (selectedItems != null) {
            for (String selectedItem : selectedItems) {

                InventoryItem invItem = inventoryItemRepository.findById(Integer.parseInt(selectedItem));
                System.out.println("INVENTORY ITEM QUANTITY --BEFORE--  REMOVE FROM CART: " + invItem.getQuantity());
                shoppingCart.removeFromCart(invItem.getBook(), 1);
                System.out.println("INVENTORY ITEM QUANTITY --AFTER-- REMOVE FROM CART: " + invItem.getQuantity());

                shoppingCartRepository.save(shoppingCart);
                shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart());

                inventoryItemRepository.save(invItem);

                System.out.println("CART ITEM FOR BOOK 1 QUANTITY:" + shoppingCart.getBooksInCart().get(0).getQuantity());
                if (shoppingCart.getBooksInCart().size()>1){
                    System.out.println("CART ITEM FOR BOOK 2 QUANTITY:" + shoppingCart.getBooksInCart().get(1).getQuantity());
                }

                System.out.println(" TOTAL IN CART: " + shoppingCart.getTotalQuantityOfCart());


                inventoryRepository.save(inventoryRepository.findById(1));

            }
        }
        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
        model.addAttribute("inventoryItems", inventoryItemRepository.findAll());
        return "home";
    }

    /**
     * Method to retrieve shopping cart for user
     * @return the shopping cart
     */
    private ShoppingCart getOrCreateShoppingCart(){
        return shoppingCartRepository.findById(1) != null ? shoppingCartRepository.findById(1) : new ShoppingCart(inventoryRepository.findById(1));
    }

}

