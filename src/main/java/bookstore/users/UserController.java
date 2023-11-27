package bookstore.users;

import bookstore.inventory.InventoryRepository;
import bookstore.inventory.ShoppingCart;
import bookstore.inventory.ShoppingCartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

/**
 * User controller
 * @author Thanuja Sivaananthan
 * @author Sabah Samwatin
 */
@Controller
public class UserController {

    @Autowired
    private final UserRepository userRepository;
    private final UsersessionRepository usersessionRepository;


    private final ShoppingCartRepository shoppingCartRepository;
    private final InventoryRepository inventoryRepository;

    private boolean userAccess = false;


    /**
     * Create new user controller
     *
     * @param userRepository user repository
     * @author Thanuja Sivaananthan
     */
    public UserController(UserRepository userRepository, ShoppingCartRepository shoppingCartRepository, InventoryRepository inventoryRepo, UsersessionRepository usersessionRepository) {
        this.userRepository = userRepository;
        this.shoppingCartRepository = shoppingCartRepository;
        this.inventoryRepository = inventoryRepo;
        this.usersessionRepository = usersessionRepository;
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
    public String createAccountForm(Model model) {
        BookUser bookUser = new BookUser();


        model.addAttribute("user", bookUser);
        return "register";
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
                model.addAttribute("errorType", "registration");
                model.addAttribute("error", "Username already exists. Please use a new username or login with the current username.");
                return "accountError";
            } else if (bookUser.getUsername().isEmpty() || bookUser.getPassword().isEmpty()) { // username/password should not be empty
                model.addAttribute("errorType", "registration");
                model.addAttribute("error", "Username or password cannot be empty.");
                return "accountError";
            } else {
                bookUser = saveUser(bookUser);
                model.addAttribute("user", bookUser);
                return "redirect:/login";
            }
        } catch (Exception e) {
            // Handle general exceptions
            model.addAttribute("errorType", "registration");
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "accountError";
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
    public String accountForm(Model model) {

        // go directly to the existing usersession - TODO display message saying already logged in?
        // otherwise, allow the user to login

        BookUser existingUser = getLoggedInUser();
        if (existingUser == null) {
            model.addAttribute("user", new BookUser());
            return "login"; // one form for both account creation and login
        } else {
            System.out.println("automatically logging into user: " + existingUser.getUsername());

            this.userAccess = true;
            model.addAttribute("user", existingUser);
            return "redirect:/listAvailableBooks"; // Redirect to user profile page
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
    public String handleUserLogin(@ModelAttribute BookUser formUser, Model model) {
        try {
            // Check if user exists
            List<BookUser> existingUsers = userRepository.findByUsername(formUser.getUsername());

            if (existingUsers.isEmpty()) {
                model.addAttribute("errorType", "login");
                model.addAttribute("error", "Username " + formUser.getUsername() + " does not exist. Please register for a new account or use a different username");
                return "accountError"; // Redirect to an account created confirmation page
            } else {
                // User exists and is attempting to log in, check password
                BookUser existingUser = existingUsers.get(0); // Assuming unique usernames
                if (existingUser.getPassword().equals(formUser.getPassword())) {
                    // Passwords match, login successful
                    model.addAttribute("user", existingUser);
                    if (usersessionRepository.findByBookUser(existingUser) == null){
                        usersessionRepository.save(new Usersession(existingUser));
                    } else {
                        System.out.println("ERROR, usersession already exists for user:" + existingUser);
                    }
                    this.userAccess = true;
                    return "redirect:/listAvailableBooks"; // Redirect to user profile page
                } else {
                    // Passwords do not match, return login error
                    model.addAttribute("errorType", "login");
                    model.addAttribute("error", "Invalid username/password");
                    return "accountError"; // Stay on the login/create account page
                }
            }
        } catch (Exception e) {
            // Handle general exceptions
            model.addAttribute("errorType", "login");
                model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
                return  "accountError"; // Redirecting back to account form
        }
    }

    public BookUser getLoggedInUser(){

        List<Usersession> usersessions = (List<Usersession>) usersessionRepository.findAll();

        BookUser loggedInUser = null;

        if (!usersessions.isEmpty()){
            loggedInUser = usersessions.get(usersessions.size()-1).getBookUser();
        }

        return loggedInUser;
    }


    /**
     * Post mapping for logout request
     * @return register-login page
     * @author Waheeb Hashmi
     */
    @PostMapping("/logout")
    public String handleUserLogout() {
        this.userAccess = false;

        BookUser bookUser = getLoggedInUser();
        System.out.println("logging out of user: " + bookUser.getUsername());
        usersessionRepository.delete(usersessionRepository.findByBookUser(bookUser));

        return "redirect:/";
    }

    /**
     * Default page is the register-login page
     * @param model     model
     * @return register-login page
     * @author Thanuja Sivaananthan
     */
    @GetMapping("/")
    public String registerLogin(Model model) {
        return "register-login";
    }
}