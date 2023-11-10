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
    @GetMapping("/createAccount")
    public String createAccountForm(Model model) {
        BookUser bookUser = new BookUser();

        model.addAttribute("user", bookUser);
        return "createAccountForm";
    }

    /**
     * Submit new account
     *
     * @param bookUser user to add
     * @param model    model
     * @return result page
     * @author Thanuja Sivaananthan
     */
    @PostMapping("/createAccount")
    public String createAccountSubmit(@ModelAttribute BookUser bookUser,
                                      Model model) {

        List<BookUser> existingUsers = userRepository.findByUsername(bookUser.getUsername());

        if (!existingUsers.isEmpty()) {
            model.addAttribute("error", "Username or password cannot be empty.");
            return "createAccountAccountError";
        } else if (bookUser.getUsername().isEmpty() || bookUser.getPassword().isEmpty()) {
            model.addAttribute("error", "Username or password cannot be empty.");
            return "createAccountAccountError";
        } else {
            if (bookUser.getUserType().equals("O")) {
                BookOwner bookOwner = new BookOwner(bookUser.getId(), bookUser.getUsername(), bookUser.getPassword());
                bookUser = bookOwner;
            } else {
                bookUser.setUserType(UserType.BOOKUSER.toString());
            }
            userRepository.save(bookUser);

            model.addAttribute("user", bookUser);

            return "createAccountResult";
        }
    }

    /**
     * Go to account
     *
     * @param model model to use
     * @return form page
     * @author Sabah Samwatin
     */
    @GetMapping("/account")
    public String accountForm(Model model) {
        model.addAttribute("user", new BookUser());
        return "accountForm"; // one form for both account creation and login
    }

    /**
     * Login to account
     *
     * @param model model to use
     * @return form page
     * @author Sabah Samwatin
     */
    @PostMapping("/account")
    public String handleUserLoginOrCreation(@ModelAttribute BookUser formUser, Model model) {
        // Check if user exists
        List<BookUser> existingUsers = userRepository.findByUsername(formUser.getUsername());

        if (existingUsers.isEmpty()) {
            // No existing user, create a new one
            if (formUser.getUsername().isEmpty() || formUser.getPassword().isEmpty()) {
                model.addAttribute("error", "Username or password cannot be empty.");
                return "accountForm"; // Redirect back to account form with error message
            }
            userRepository.save(formUser);
            model.addAttribute("user", formUser);
            return "accountCreated"; // Redirect to an account created confirmation page
        } else {
            // Check if the user is trying to create a new account with an existing username
            if (formUser.getId() == null || formUser.getId() == 0) {
                model.addAttribute("error", "Username already exists. Please choose a different one.");
                return "accountForm"; // Redirect back to account form with error message
            }
            // User exists and is attempting to log in, check password
            BookUser existingUser = existingUsers.get(0); // Assuming unique usernames
            if (existingUser.getPassword().equals(formUser.getPassword())) {
                // Passwords match, login successful
                model.addAttribute("user", existingUser);
                return "userProfile"; // Redirect to user profile page
            } else {
                // Passwords do not match, return login error
                model.addAttribute("loginError", "Invalid password");
                return "accountForm"; // Stay on the login/create account page
            }
        }
    }
}
