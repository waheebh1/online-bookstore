package bookstore.inventory;

import bookstore.users.BookUser;
import bookstore.users.UserController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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
    private UserController userController;
    private boolean checkoutFlag = false;

    /**
     * Constructor for checkout controller
     *
     * @param authorRepo repository of authors
     * @param bookRepo   repository of books
     * @author Shrimei Chock
     * @author Maisha Abdullah
     */
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo, ShoppingCartRepository shoppingCartRepository, ShoppingCartItemRepository shoppingCartItemRepository, UserController userController) {
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.inventoryRepository = inventoryRepo;
        this.inventoryItemRepository = inventoryItemRepo;
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.userController = userController;
    }

    /**
     * List all books in inventory
     *
     * @param model container
     * @return reroute to html page to display all books
     * @author Maisha Abdullah
     * @author Thanuja Sivaananthan
     * @author Shrimei Chock
     */
    @GetMapping("/listAvailableBooks")
    public String listAvailableBooks
    (@RequestParam(name = "searchValue", required = false, defaultValue = "") String searchValue,
     @RequestParam(name = "sort", required = false, defaultValue = "low_to_high") String sort,
     @RequestParam(name = "author", required = false) List<String> authors,
     @RequestParam(name = "genre", required = false) List<String> genres,
     @RequestParam(name = "publisher", required = false) List<String> publishers,
     Model model) {
        checkoutFlag = false;
        if(this.userController.getUserAccess()){
            BookUser loggedInUser = userController.getLoggedInUser();

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
            } else if (sort.equals(SortCriteria.ALPHABETICAL.label)) {
                inventoryItems.sort(Comparator.comparing(item -> item.getBook().getTitle()));
            } else {
                System.out.println("ERROR: Sort criteria not found");
            }

            //filter
            List<Book> bookList = BookFiltering.createBookList(inventoryItems);
            List<String> authorList = BookFiltering.getAllAuthors(bookList);
            List<String> genreList = BookFiltering.getAllGenres(bookList);
            List<String> publisherList = BookFiltering.getAllPublishers(bookList);
            //TODO add for price ranges

            //Print checked values
            System.out.println("Authors: " + authors);
            System.out.println("Genres: " + genres);
            System.out.println("Publishers: " + publishers);

            inventoryItems = BookFiltering.getItemsMatchingFilters(inventoryItems, authors, genres, publishers);

            model.addAttribute("user", loggedInUser);
            model.addAttribute("inventoryItems", inventoryItems);
            model.addAttribute("sort", sort);
            model.addAttribute("authors", authorList);
            model.addAttribute("genres", genreList);
            model.addAttribute("publishers", publisherList);
            return "home";
        } else {
            return "access-denied";
        }
    }

    /**
     * View details for a single book
     * @param model container
     * @return route to html page to display contents of a book when clicked
     * @author Shrimei Chock
     */
    @GetMapping("/viewBook")
    public String viewBook(@RequestParam(name = "isbn") String isbn, Model model) {
        if(!this.userController.getUserAccess()){
            return "access-denied";
        }
        Book bookToDisplay = bookRepository.findByIsbn(isbn);
        model.addAttribute("book", bookToDisplay);
        return "book-info";
    }

    /**
     * Method to go to an add to cart form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @GetMapping("/addToCart")
    public String addToCartForm(Model model) {

        if(!this.userController.getUserAccess()){
            return "access-denied";
        }

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
        BookUser loggedInUser = userController.getLoggedInUser();

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

        List<InventoryItem> inventoryItems = (List<InventoryItem>) inventoryItemRepository.findAll();
        inventoryItems = BookFiltering.getItemsInStock(inventoryItems);

        List<Book> bookList = BookFiltering.createBookList(inventoryItems);
        List<String> authorList = BookFiltering.getAllAuthors(bookList);
        List<String> genreList = BookFiltering.getAllGenres(bookList);
        List<String> publisherList = BookFiltering.getAllPublishers(bookList);

        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("authors", authorList); //TODO repetition
        model.addAttribute("genres", genreList);
        model.addAttribute("publishers", publisherList);
        return "home"; //TODO after add/remove from cart, the sort goes away. Need to store the sort value, redirect?

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
        if(!this.userController.getUserAccess()){
            return "access-denied";
        }

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

        BookUser loggedInUser = userController.getLoggedInUser();

        //TODO - if user does not already have a shopping cart
        ShoppingCart shoppingCart = getOrCreateShoppingCart();

        if (selectedItems != null) {
            for (String selectedItem : selectedItems) {
                if(checkoutFlag){
                    ShoppingCartItem cartItem = shoppingCartItemRepository.findById(Integer.parseInt(selectedItem));
                    if (cartItem != null) {
                        shoppingCart.removeFromCart(cartItem.getBook(), 1);
                        shoppingCartItemRepository.delete(cartItem);
                    }
                
                } else {
                InventoryItem invItem = inventoryItemRepository.findById(Integer.parseInt(selectedItem));
                System.out.println("INVENTORY ITEM QUANTITY --BEFORE--  REMOVE FROM CART: " + invItem.getQuantity());
                shoppingCart.removeFromCart(invItem.getBook(), 1);
                System.out.println("INVENTORY ITEM QUANTITY --AFTER-- REMOVE FROM CART: " + invItem.getQuantity());

                shoppingCartRepository.save(shoppingCart);
                shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart());
                
                for (ShoppingCartItem shoppingCartItem : shoppingCartItemRepository.findByQuantity(0)){
                    System.out.println("FOUND EMPTY ITEM, WILL DELETE " + shoppingCartItem.getBook().getTitle());
                    shoppingCartItemRepository.delete(shoppingCartItem);
                }

                inventoryItemRepository.save(invItem);

                if (!shoppingCart.getBooksInCart().isEmpty()) {
                    System.out.println("CART ITEM FOR BOOK 1 QUANTITY:" + shoppingCart.getBooksInCart().get(0).getQuantity());
                    if (shoppingCart.getBooksInCart().size() > 1) {
                        System.out.println("CART ITEM FOR BOOK 2 QUANTITY:" + shoppingCart.getBooksInCart().get(1).getQuantity());
                    }
                }

                System.out.println(" TOTAL IN CART: " + shoppingCart.getTotalQuantityOfCart());


                inventoryRepository.save(inventoryRepository.findById(1));

                }
            }
        }

        List<InventoryItem> inventoryItems = (List<InventoryItem>) inventoryItemRepository.findAll();
        inventoryItems = BookFiltering.getItemsInStock(inventoryItems);

        List<Book> bookList = BookFiltering.createBookList(inventoryItems);
        List<String> authorList = BookFiltering.getAllAuthors(bookList);
        List<String> genreList = BookFiltering.getAllGenres(bookList);
        List<String> publisherList = BookFiltering.getAllPublishers(bookList);

        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
        model.addAttribute("inventoryItems", inventoryItemRepository.findAll());
        model.addAttribute("authors", authorList); //TODO repetition
        model.addAttribute("genres", genreList);
        model.addAttribute("publishers", publisherList);

        if(checkoutFlag){
    
            //Calculate total price again
            double totalPrice = 0;
            for (ShoppingCartItem item : shoppingCart.getBooksInCart()) {
                totalPrice += item.getBook().getPrice() * item.getQuantity();
            }
    
            double roundedPrice = Math.round(totalPrice * 100.0) / 100.0;
            
            model.addAttribute("items", shoppingCart.getBooksInCart());
            model.addAttribute("totalPrice", roundedPrice);
    
            if (this.userController.getUserAccess()) {
                return "checkout";
            } else {
                return "access-denied";
            }
        }
        return "home";    
}

    /**
     * Method to retrieve shopping cart for user
     * @return the shopping cart
     * @author Maisha Abdullah
     * @author Thanuja Sivaananthan
     */
    private ShoppingCart getOrCreateShoppingCart(){
        BookUser bookUser = userController.getLoggedInUser();
        if (bookUser != null && bookUser.getShoppingCart() != null){
            return bookUser.getShoppingCart();
        } else { // This shouldn't happen
            System.out.println("ERROR, USER OR SHOPPINGCART IS NULL");
            return new ShoppingCart(inventoryRepository.findById(1));
        }
    }

    /** 
    * Method to get checkout page
    * @param model container
    * @return route to html page to display checkout page or access denied page
    * @author Waheeb Hashmi
    */
    @GetMapping("/checkout")
    public String viewCart(Model model) {

        if(!this.userController.getUserAccess()){
            return "access-denied";
        }

        checkoutFlag = true;
        ShoppingCart shoppingCart = getOrCreateShoppingCart();

        //Calculate total price
        double totalPrice = 0;
        for (ShoppingCartItem item : shoppingCart.getBooksInCart()) {
            totalPrice += item.getBook().getPrice() * item.getQuantity();
        }

        double roundedPrice = Math.round(totalPrice * 100.0) / 100.0;
        
        model.addAttribute("items", shoppingCart.getBooksInCart());
        model.addAttribute("totalPrice", roundedPrice);

        return "checkout";
   }

    /**
    * Method to process checkout
    * @param model container
    * @return route to html page to display order confirmation page
    * @author Waheeb Hashmi
    */
    @PostMapping("/checkout")
    public String confirmOrder(Model model) {
        // Generate a random confirmation number
        String confirmationNumber = UUID.randomUUID().toString();
        model.addAttribute("confirmationNumber", confirmationNumber);

        ShoppingCart shoppingCart = getOrCreateShoppingCart();

        shoppingCart.checkout();

        shoppingCartRepository.save(shoppingCart);
        shoppingCartItemRepository.deleteAll(shoppingCartItemRepository.findByQuantity(0));
        shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart()); // not sure if we need this
        inventoryRepository.save(inventoryRepository.findById(1));

        return "order-confirmation";
    }
}
