package bookstore.users;

import bookstore.inventory.Book;
import bookstore.inventory.InventoryRepository;
import bookstore.inventory.ShoppingCart;
import bookstore.inventory.ShoppingCartRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User controller
 * @author Thanuja Sivaananthan
 * @author Sabah Samwatin
 */
@Controller
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    private final ShoppingCartRepository shoppingCartRepository;
    private final InventoryRepository inventoryRepository;

    private boolean userAccess = false;

    private static final String cookieUsername = "username";

    /**
     * Create new user controller
     *
     * @param userRepository user repository
     * @author Thanuja Sivaananthan
     */
    public UserController(UserRepository userRepository, ShoppingCartRepository shoppingCartRepository, InventoryRepository inventoryRepo) {
        this.userRepository = userRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.inventoryRepository = inventoryRepo;
    }

    /**
     * Gets status of user access (true meaning has access to system, false meaning not logged in)
     * @author Waheeb Hashmi
     */
    public boolean getUserAccess(){
        return userAccess;
    }
    
    /**
     * Create new account
     *
     * @param model model to use
     * @return form page
     * @author Thanuja Sivaananthan
     */
    @GetMapping("/register")
    public String createAccountForm(HttpServletRequest request, HttpServletResponse response, Model model) {

        // if usersesion exists, redirect to that session
        BookUser loggedInUser = getLoggedInUser(request.getCookies());
        if (loggedInUser != null){
            model.addAttribute("user", loggedInUser);

            return "redirect:/listAvailableBooks";
        } else {
            BookUser bookUser = new BookUser();

            model.addAttribute("user", bookUser);
            return "register";
        }
    }


    /**
     * Save a user
     * @param bookUser      the book user to add
     * @return              the modified/saved user
     */
    private BookUser saveUser(BookUser bookUser){
        if (bookUser.getUserType() == UserType.BOOKOWNER) {
            // setup as owner if specified
            bookUser = new BookOwner(bookUser.getId(), bookUser.getUsername(), bookUser.getPassword(), bookUser.getShoppingCart());
        }

        // need to save shoppingCarts first, then set shoppingCart for the user, then save the user
        ShoppingCart shoppingCart = new ShoppingCart(inventoryRepository.findById(1));
        shoppingCartRepository.save(shoppingCart);
        bookUser.setShoppingCart(shoppingCart);
        userRepository.save(bookUser);

        return bookUser;
    }

    /**
     * Submit new account
     *
     * @param bookUser user to add
     * @param model    model
     * @return result page
     * @author Thanuja Sivaananthan
     */
    @PostMapping("/register")
    public String createAccountSubmit(@ModelAttribute BookUser bookUser,
                                      Model model) {

        try {
            List<BookUser> existingUsers = userRepository.findByUsername(bookUser.getUsername());

            if (!existingUsers.isEmpty()) { // only allow accounts with unique usernames
                model.addAttribute("user", new BookUser());
                model.addAttribute("registrationError", "Username already exists. Please use a new username or login with the current username.");
                return "register"; // Return to the registration page
            } else if (bookUser.getUsername().isEmpty() || bookUser.getPassword().isEmpty()) { // username/password should not be empty
                model.addAttribute("user", new BookUser());
                model.addAttribute("registrationError", "Username or password cannot be empty.");
                return "register"; // Return to the registration page
            } else {
                bookUser = saveUser(bookUser);
                model.addAttribute("user", bookUser);
                return "redirect:/login";
            }
        } catch (Exception e) {
            // Handle general exceptions
            model.addAttribute("user", new BookUser());
            model.addAttribute("registrationError", "An unexpected error occurred: " + e.getMessage());
            return "register"; // Return to the registration page
        }
    }

    /**
     * Go to account
     *
     * @param model model to use
     * @return form page
     * @author Sabah Samwatin
     */
    @GetMapping("/login")
    public String accountForm(HttpServletRequest request, HttpServletResponse response, Model model) {

        // if usersesion exists, redirect to that session
        BookUser loggedInUser = getLoggedInUser(request.getCookies());
        if (loggedInUser != null){
            model.addAttribute("user", loggedInUser);

            return "redirect:/listAvailableBooks";
        } else {
            model.addAttribute("user", new BookUser());
            return "login";
        }
    }

    /**
     * Login to account
     *
     * @param model model to use
     * @return form/result page
     * @author Sabah Samwatin
     */
    @PostMapping("/login")
    public String handleUserLogin(HttpServletRequest request, HttpServletResponse response, @ModelAttribute BookUser formUser, Model model) {
        try {
            // Check if user exists
            List<BookUser> existingUsers = userRepository.findByUsername(formUser.getUsername());

            if (existingUsers.isEmpty()) {
                model.addAttribute("user", new BookUser());
                model.addAttribute("loginError", "Username " + formUser.getUsername() + " does not exist. Please register for a new account or use a different username");
                return "login"; // Redirect to the login page
            } else {
                // User exists and is attempting to log in, check password
                BookUser existingUser = existingUsers.get(0); // Assuming unique usernames
                if (existingUser.getPassword().equals(formUser.getPassword())) {
                    // Passwords match, login successful
                    model.addAttribute("user", existingUser);

                    System.out.println("ADDING COOKIE " + existingUser.getUsername());
                    Cookie addCookie = new Cookie(cookieUsername, existingUser.getUsername());
                    addCookie.setPath("/");
                    response.addCookie(addCookie);
                    this.userAccess = true;
                    return "redirect:/listAvailableBooks"; // Redirect to user profile page
                } else {
                    // Passwords do not match, return login error
                    model.addAttribute("user", new BookUser());
                    model.addAttribute("loginError", "Invalid username/password");
                    return "login"; // Stay on the login page
                }
            }
        } catch (Exception e) {
            // Handle general exceptions
            model.addAttribute("user", new BookUser());
            model.addAttribute("loginError", "An unexpected error occurred: " + e.getMessage());
            return "login"; // Redirecting back to the login form
        }
    }

    public BookUser getLoggedInUser(Cookie[] cookies){

        String loggedInUsername = retreiveCookie(cookies);
        BookUser loggedInUser = null;

        if (loggedInUsername != null) {
            System.out.println("COOKIE RETRIEVED: " + loggedInUsername);

            List<BookUser> loggedInUsers = userRepository.findByUsername(loggedInUsername);

            if (!loggedInUsers.isEmpty()) {
                loggedInUser = loggedInUsers.get(0);
                System.out.println("USER RETRIEVED: " + loggedInUser.getUsername());
            }
        }

        return loggedInUser;
    }

    private static String retreiveCookie(Cookie[] cookies){
        String result = null;
        if (cookies == null){
            return null;
        }
        for (Cookie cookie : cookies){
            if (cookie.getName().equals(cookieUsername) && cookie.getMaxAge() != 0){
                result = cookie.getValue();
            }
        }
        return result;
    }


    /**
     * Post mapping for logout request
     * @return register-login page
     * @author Waheeb Hashmi
     */
    @PostMapping("/logout")
    public String handleUserLogout(HttpServletResponse response, HttpServletRequest request) {
        this.userAccess = false;

        Cookie[] cookies = request.getCookies();
        if (cookies != null){
            System.out.println("CHECK FOR COOKIES");
            for (Cookie cookie : cookies){
                System.out.println("NAME: " + cookie.getName());
                System.out.println("VALUE: " + cookie.getValue());
                if (cookie.getName().equals(cookieUsername)){ // What does this do?
                    System.out.println("logging out of user: " + cookie.getValue());

                    System.out.println("ADDING REMOVE COOKIE");
                    Cookie removeCookie = new Cookie(cookieUsername, "");
                    removeCookie.setMaxAge(0);
                    removeCookie.setPath("/");
                    response.addCookie(removeCookie);
                    break;
                }
            }
        } else {
            System.out.println("ERROR, COOKIES NULL");
        }

        return "redirect:/";
    }

    /**
     * Default page is the register-login page
     * @param model     model
     * @return register-login page
     * @author Thanuja Sivaananthan
     */
    @GetMapping("/")
    public String registerLogin(HttpServletRequest request, HttpServletResponse response, Model model) {
        // if usersesion exists, redirect to that session
        BookUser loggedInUser = getLoggedInUser(request.getCookies());
        if (loggedInUser != null){
            model.addAttribute("user", loggedInUser);

            return "redirect:/listAvailableBooks";
        } else {
            return "register-login";
        }
    }


    /**
     * Method to calculate jaccard distance using two sets of books
     * @param user1Books
     * @param user2Books
     * @return
     */
    public double calculateJaccardDistance(Set<Book> user1Books, Set<Book> user2Books) {
        Set<Book> intersection = new HashSet<>(user1Books);
        intersection.retainAll(user2Books);

        Set<Book> union = new HashSet<>(user1Books);
        union.addAll(user2Books);
        
        return 1.0 - ((double) intersection.size() / union.size());
    }
}