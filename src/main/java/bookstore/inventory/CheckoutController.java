package bookstore.inventory;

import bookstore.users.BookUser;
import bookstore.users.UserController;
import bookstore.users.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.util.stream.Collectors;

@Controller
public class CheckoutController {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;
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
    public CheckoutController(AuthorRepository authorRepo, BookRepository bookRepo, InventoryRepository inventoryRepo, InventoryItemRepository inventoryItemRepo, ShoppingCartRepository shoppingCartRepository, ShoppingCartItemRepository shoppingCartItemRepository, UserController userController, UserRepository userRepository) {
        this.authorRepository = authorRepo;
        this.bookRepository = bookRepo;
        this.inventoryRepository = inventoryRepo;
        this.inventoryItemRepository = inventoryItemRepo;
        this.shoppingCartRepository = shoppingCartRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.userController = userController;
        this.userRepository = userRepository;
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
    (HttpServletRequest request, HttpServletResponse response,
     @RequestParam(name = "searchValue", required = false, defaultValue = "") String searchValue,
     @RequestParam(name = "sort", required = false, defaultValue = "low_to_high") String sort,
     @RequestParam(name = "author", required = false) List<String> authors,
     @RequestParam(name = "genre", required = false) List<String> genres,
     @RequestParam(name = "publisher", required = false) List<String> publishers,
     @RequestParam(name = "priceRange", required = false) String price, //default is max price
     Model model) {
        checkoutFlag = false;
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if(loggedInUser != null){

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
            String min_price = BookFiltering.getBookWithLowestPrice(bookList).getPrice().toString();
            String max_price = BookFiltering.getBookWithHighestPrice(bookList).getPrice().toString();

            //price stuff
            if (price == null){
                price = max_price;
            }
            System.out.println("---PRICE: " + price);

            inventoryItems = BookFiltering.getItemsMatchingFilters(inventoryItems, authors, genres, publishers, Double.parseDouble(price));
            List<Book> x = recommendBooks(loggedInUser.getId());

            model.addAttribute("books", x);
            model.addAttribute("user", loggedInUser);
            model.addAttribute("inventoryItems", inventoryItems);
            model.addAttribute("sort", sort);
            model.addAttribute("authors", authorList);
            model.addAttribute("genres", genreList);
            model.addAttribute("publishers", publisherList);
            model.addAttribute("min", min_price);
            model.addAttribute("max", max_price);
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
    public String viewBook(HttpServletRequest request, HttpServletResponse response,
                           @RequestParam(name = "isbn") String isbn, Model model) {
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if(loggedInUser == null){
            return "access-denied";
        }
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            String authors = book.getAuthor().stream()
                    .map(author -> author.getFirstName() + " " + author.getLastName())
                    .collect(Collectors.joining(", "));
            model.addAttribute("book", book);
            model.addAttribute("authors", authors);

            // Determine the user type
            String userType = loggedInUser.getUserType().name();
            // Store the userType in the session
            model.addAttribute("userType", userType);

            return "book-info";
        }
        return "redirect:/"; // if the book doesn't exist, redirect to the home page

    }

    /**
     * Method to go to an add to cart form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @GetMapping("/addToCart")
    public String addToCartForm(HttpServletRequest request, HttpServletResponse response,
                                Model model) {

        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if(loggedInUser == null){
            return "access-denied";
        }
        List<Book> x = recommendBooks(loggedInUser.getId());
        model.addAttribute("inventory", inventoryItemRepository.findAll());
        model.addAttribute("books", x);
        return "home";
    }

    /**
     * Method to submit an add to cart form
     * @param selectedItems the items selected from the checklist in the form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     * @author Shrimei Chock
     */
    @PostMapping("/addToCart")
    public String addToCart(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(name = "selectedItems", required = false) String[] selectedItems, Model model) {
        
        System.out.println("-- ADD TO CART METHOD --");
        System.out.println("\tSELECTED ITEM: " + Arrays.toString(selectedItems));

        //get user and their shopping cart
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        ShoppingCart shoppingCart = loggedInUser.getShoppingCart();

        if (selectedItems != null) {
            for (String selectedItem : selectedItems) {
                if (checkoutFlag) {
                    ShoppingCartItem cartItem = shoppingCartItemRepository.findById(Integer.parseInt(selectedItem));
                    if (cartItem != null) {
                        shoppingCart.addToCart(cartItem.getBook(), 1);
//                        shoppingCartItemRepository.save(cartItem);
                    }
                } else {

                    //find item in inventory and add to cart
                    InventoryItem invItem = inventoryItemRepository.findById(Integer.parseInt(selectedItem));
                    System.out.println("\tINVENTORY ITEM QUANTITY --BEFORE-- ADD TO CART: " + invItem.getQuantity());
                    shoppingCart.addToCart(invItem.getBook(), 1);
                    System.out.println("\tINVENTORY ITEM QUANTITY --AFTER-- ADD TO CART: " + invItem.getQuantity());

                    inventoryItemRepository.save(invItem);

                    //print number of books in cart
                    System.out.println("\tTOTAL IN CART: " + shoppingCart.getTotalQuantityOfCart());
                }
                //update the user's shopping cart and inventory
                shoppingCartRepository.save(shoppingCart);
                shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart());
                inventoryRepository.save(inventoryRepository.findById(1));
            }
        }

        //if on checkout page, recalculate total price
        if(checkoutFlag){
            double totalPrice = 0;
            for (ShoppingCartItem item : shoppingCart.getBooksInCart()) {
                totalPrice += item.getBook().getPrice() * item.getQuantity();
            }

            double roundedPrice = Math.round(totalPrice * 100.0) / 100.0;

//            model.addAttribute("items", shoppingCart.getBooksInCart());
//            model.addAttribute("totalPrice", roundedPrice);

            return "redirect:/checkout";
        }
      
        return "redirect:/listAvailableBooks";
    }

    /**
     * Method to get the total in cart and update it on the html
     * @return the total in the cart
     * @author Maisha Abdullah
     */
    @GetMapping("/getTotalInCart")
    @ResponseBody
    public int getTotalInCart(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("going into get total in cart");

        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        ShoppingCart shoppingCart = loggedInUser.getShoppingCart();

        return shoppingCart.getTotalQuantityOfCart();
    }

    /**
     * Method to go to remove from cart form
     * @param model container
     * @return route to html page to display home page with list of available books
     * @author Maisha Abdullah
     */
    @GetMapping("/removeFromCart")
    public String removeFromCartForm (HttpServletRequest request, HttpServletResponse response,
                                      Model model){
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if(loggedInUser == null){
            return "access-denied";
        }

        model.addAttribute("inventory", inventoryItemRepository.findAll());

//        List<Book> x = recommendBooks(loggedInUser.getId());
//        model.addAttribute("books", x);

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
    public String removeFromCart (HttpServletRequest request, HttpServletResponse response,
                                  @RequestParam(name = "selectedItems", required = false) String[]selectedItems, Model
            model){

        System.out.println("-- REMOVE FROM CART METHOD --");
        System.out.println("\tSELECTED ITEM: " + Arrays.toString(selectedItems));

        //get user and their shopping cart
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        ShoppingCart shoppingCart = loggedInUser.getShoppingCart();

        if (selectedItems != null) {
            for (String selectedItem : selectedItems) {

                //if user is removing from checkout page
                if(checkoutFlag){
                    ShoppingCartItem cartItem = shoppingCartItemRepository.findById(Integer.parseInt(selectedItem));
                    if (cartItem != null) {
                        shoppingCart.removeFromCart(cartItem.getBook(), 1);
                        shoppingCartItemRepository.delete(cartItem);
                    }
                } else {
                    //find item in inventory and remove from cart
                    InventoryItem invItem = inventoryItemRepository.findById(Integer.parseInt(selectedItem));
                    System.out.println("\tINVENTORY ITEM QUANTITY --BEFORE--  REMOVE FROM CART: " + invItem.getQuantity());
                    shoppingCart.removeFromCart(invItem.getBook(), 1);
                    System.out.println("\tINVENTORY ITEM QUANTITY --AFTER-- REMOVE FROM CART: " + invItem.getQuantity());

                    //remove items from cart that have a quantity of 0
                    for (ShoppingCartItem shoppingCartItem : shoppingCartItemRepository.findByQuantity(0)){
                        System.out.println("\tFOUND EMPTY ITEM, WILL DELETE " + shoppingCartItem.getBook().getTitle());
                        shoppingCartItemRepository.delete(shoppingCartItem);
                    }

                    //update inventory
                    inventoryItemRepository.save(invItem);

                    System.out.println("\tTOTAL IN CART: " + shoppingCart.getTotalQuantityOfCart());
                }

                //update shopping cart in repo
                shoppingCartRepository.save(shoppingCart);
                shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart());

                //update inventory
                inventoryRepository.save(inventoryRepository.findById(1));
            }
        }
      
        //if on checkout page, recalculate total price
        if(checkoutFlag){
            double totalPrice = 0;
            for (ShoppingCartItem item : shoppingCart.getBooksInCart()) {
                totalPrice += item.getBook().getPrice() * item.getQuantity();
            }
    
            double roundedPrice = Math.round(totalPrice * 100.0) / 100.0;
            
//            model.addAttribute("items", shoppingCart.getBooksInCart());
//            model.addAttribute("totalPrice", roundedPrice);
    
            return "redirect:/checkout";
        }

        return "redirect:/listAvailableBooks";
    }

    /** 
    * Method to get checkout page
    * @param model container
    * @return route to html page to display checkout page or access denied page
    * @author Waheeb Hashmi
    */
    @GetMapping("/checkout")
    public String viewCart(HttpServletRequest request, HttpServletResponse response,
                           Model model) {

        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if(loggedInUser == null){
            return "access-denied";
        }

        checkoutFlag = true;
        ShoppingCart shoppingCart = loggedInUser.getShoppingCart();

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
    public String confirmOrder(HttpServletRequest request, HttpServletResponse response, Model model) {
        // Generate a random confirmation number
        String confirmationNumber = UUID.randomUUID().toString();
        model.addAttribute("confirmationNumber", confirmationNumber);
        model.addAttribute("confirmationMessage", "Order Completed!");

        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        ShoppingCart shoppingCart = loggedInUser.getShoppingCart();

        shoppingCart.checkout();

        shoppingCartRepository.save(shoppingCart);
        // shoppingCartItemRepository.deleteAll(shoppingCartItemRepository.findByQuantity(0));
        // shoppingCartItemRepository.saveAll(shoppingCart.getBooksInCart()); // not sure if we need this
        inventoryRepository.save(inventoryRepository.findById(1));

        return "order-confirmation";
    }

    /**
     * Method that recommends the books based on the jaccard dizstance between a user and other users
     * @author Waheeb Hashmi
     * @param userId user's id
     * @return ArrayList<Book>
     */
  public ArrayList<Book> recommendBooks(Long userId) {
        Set<Book> recommendedBooks = new HashSet<>();
        if(userId != null){
            Set<Book> userBooks = getBooksInCartByUserId(userId);
            if (!userBooks.isEmpty()) {

            Map<Long, Double> userDistances = new HashMap<>();

            for (BookUser otherUser : userRepository.findAll()) {
                if (!otherUser.getId().equals(userId)) {
                    Set<Book> otherUserBooks = getBooksInCartByUserId(otherUser.getId());
                    double distance = userController.calculateJaccardDistance(userBooks, otherUserBooks);
                    if (distance < 1) {
                        userDistances.put(otherUser.getId(), distance);
                    }
                }
            }

            List<Long> similarUserIds = new ArrayList<>();
            List<Map.Entry<Long, Double>> entries = new ArrayList<>(userDistances.entrySet());
            entries.sort(Map.Entry.comparingByValue());

            for (Map.Entry<Long, Double> entry : entries) {
                similarUserIds.add(entry.getKey());
            }
            
        for (Long similarUserId : similarUserIds) {
            Set<Book> books = getBooksInCartByUserId(similarUserId);
            books.removeAll(userBooks);
            recommendedBooks.addAll(books);
        }
    }
}
    return new ArrayList<>(recommendedBooks);
}

   /**
    * Method that gets the books in the shopping cart by user id
    * @author Waheeb Hashmi
    * @param userId user's id
    * @return Set<Book>
    */
   public Set<Book> getBooksInCartByUserId(long userId) {
    BookUser user = userRepository.findById(userId);
    ShoppingCart shoppingCart = user.getShoppingCart();
    Set<Book> books = new HashSet<>();
    for (ShoppingCartItem item : shoppingCart.getBooksForRecommendations()) {
        books.add(item.getBook());
        }
    return books;
}

}
