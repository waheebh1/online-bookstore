package bookstore.inventory;

import bookstore.users.BookUser;
import bookstore.users.UserController;
import bookstore.users.UserRepository;

import java.util.Arrays;
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
     */
    @GetMapping("/listAvailableBooks")
    public String listAvailableBooks
    (@RequestParam(name = "searchValue", required = false, defaultValue = "") String searchValue, Model model) {
       checkoutFlag = false;
        if(this.userController.getUserAccess()){
            BookUser loggedInUser = userController.getLoggedInUser();

            Inventory inventory = inventoryRepository.findById(1); // assuming one inventory

            List<InventoryItem> inventoryItems;
            if (searchValue.isEmpty()) {
                inventoryItems = inventory.getAvailableBooks();
            } else {
                inventoryItems = inventory.getBooksMatchingSearch(searchValue);
                if (inventoryItems.isEmpty()) {
                    model.addAttribute("error", "No items match \"" + searchValue + "\".");
                }
            }

            model.addAttribute("user", loggedInUser);
            model.addAttribute("inventoryItems", inventoryItems);
            return "home";
        } else {
            return "access-denied";
        } 
    }

    /**
     * View details for a single book
     *
     * @param model container
     * @return route to html page to display contents of a book when clicked
     */
    @GetMapping("/viewBook")
    public String viewBook(@RequestParam(name = "isbn") String isbn, Model model) { //TODO pass in isbn when calling this endpoint
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
        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
        model.addAttribute("inventoryItems", inventoryItemRepository.findAll());
        return "home";
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

        model.addAttribute("user", loggedInUser);
        model.addAttribute("totalInCart", shoppingCart.getTotalQuantityOfCart());
        model.addAttribute("inventoryItems", inventoryItemRepository.findAll());

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
        } else {
            System.out.println("ERROR, USER OR SHOPPINGCART IS NULL");
            return new ShoppingCart(inventoryRepository.findById(1));
        }
//        return shoppingCartRepository.findById(1) != null ? shoppingCartRepository.findById(1) : new ShoppingCart(inventoryRepository.findById(1));
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
        shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart());
        inventoryRepository.save(inventoryRepository.findById(1));

        return "order-confirmation";
    }
}
