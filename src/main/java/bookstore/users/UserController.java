package bookstore.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
