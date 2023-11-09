package bookstore.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;

@Controller
public class UserController {

    private final UserRepository userRepository;


    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/createAccount")
    public String createAccountForm(Model model) {

        model.addAttribute("user", new BookUser());
        return "createAccountForm";
    }

    @PostMapping("/createAccount")
    public String createAccountSubmit(@ModelAttribute BookUser bookUser,
                                       Model model)
    {
        userRepository.save(bookUser);
        model.addAttribute("user", bookUser);

        return "success";
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
