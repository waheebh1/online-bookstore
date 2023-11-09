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

    private final UserRepository userRepository;


    /**
     * Create new user controller
     * @author Thanuja Sivaananthan
     * @param userRepository   user repository
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create new account
     * @author Thanuja Sivaananthan
     * @param model model to use
     * @return form page
     */
    @GetMapping("/createAccount")
    public String createAccountForm(Model model) {
        BookUser bookUser = new BookUser();

        model.addAttribute("user", bookUser);
        return "createAccountForm";
    }

    /**
     * Submit new account
     * @author Thanuja Sivaananthan
     * @param bookUser  user to add
     * @param model     model
     * @return          result page
     */
    @PostMapping("/createAccount")
    public String createAccountSubmit(@ModelAttribute BookUser bookUser,
                                       Model model)
    {

        if (bookUser.getUserType().equals("O")){
            BookOwner bookOwner = new BookOwner(bookUser.getId(), bookUser.getUsername(), bookUser.getPassword());
            bookUser = bookOwner;
        } else {
            bookUser.setUserType(UserType.BOOKUSER.toString());
        }
        userRepository.save(bookUser);

        model.addAttribute("user", bookUser);

        return "createAccountResult";
    }

    @GetMapping("/account")
    public String accountForm (Model model){
        model.addAttribute("user", new BookUser());
        return "accountForm"; // one form for both account creation and login
    }
    @PostMapping("/account")
    public String handleUserLoginOrCreation(@ModelAttribute BookUser formUser, Model model) {
        // Check if user exists
        List<BookUser> existingUsers = userRepository.findByUsername(formUser.getUsername());

        if (existingUsers.isEmpty()) {
            // No existing user, create a new one
            userRepository.save(formUser);
            model.addAttribute("user", formUser);
            return "accountCreated"; // Redirect to an account created confirmation page
        } else {
            // User exists, check password
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
