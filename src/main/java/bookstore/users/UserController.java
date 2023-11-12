package bookstore.users;

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


    /**
     * Create new user controller
     *
     * @param userRepository user repository
     * @author Thanuja Sivaananthan
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            bookUser = new BookOwner(bookUser.getId(), bookUser.getUsername(), bookUser.getPassword());
        }
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

                return "createAccountResult";
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
        model.addAttribute("user", new BookUser());
        return "login"; // one form for both account creation and login
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
                    return "home"; // Redirect to user profile page // TODO - redirect:/home
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