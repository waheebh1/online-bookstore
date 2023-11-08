package bookstore.users;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
