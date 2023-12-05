package bookstore.inventory;

import bookstore.users.BookUser;
import bookstore.users.UserController;
import bookstore.users.UserType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/book")
public class BookController {

    private final BookRepository bookRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    private UserController userController;

    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    /**
     * Constructor for BookController
     * @param bookRepository
     * @param inventoryItemRepository
     * @param inventoryRepository
     * @author Sabah Samwatin, Thanuja Sivaananthan
     */
    @Autowired
    public BookController(BookRepository bookRepository, InventoryItemRepository inventoryItemRepository, InventoryRepository inventoryRepository) {
        this.bookRepository = bookRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Handler method to display the form for uploading a new book
     * @param model
     * @return form
     * @author Sabah Samwatin, Thanuja Sivaananthan
     */
    @GetMapping("/upload")
    public String showUploadForm(Model model, HttpServletRequest request, HttpServletResponse response) {
        // Check if an owner is logged in
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if (loggedInUser == null || !loggedInUser.getUserType().equals(UserType.BOOKOWNER)) {
            return "access-denied";
        }

        model.addAttribute("book", new Book());
        return "uploadBook";
    }

    /**
     * Handler method to process the upload form
     * @param book
     * @param authorsInput
     * @return list of available books
     * @author Sabah Samwatin, Thanuja Sivaananthan
     */
    @PostMapping("/upload")
    public String handleUploadForm(@ModelAttribute Book book, @RequestParam String authorsInput) {
        String[] authorNames = authorsInput.split(",");
        ArrayList<Author> authors = new ArrayList<>();

        for (String fullName : authorNames) {
            String[] parts = fullName.trim().split("\\s+");
            String firstName = parts[0];
            String lastName = (parts.length > 1) ? parts[1] : "";

            List<Author> foundAuthors = authorRepository.findByFirstNameAndLastName(firstName, lastName);
            Author author;
            if (foundAuthors.isEmpty()) {
                author = new Author(firstName, lastName);
                authorRepository.save(author);
            } else {
                // Here we just take the first found author
                author = foundAuthors.get(0);
            }
            authors.add(author);
        }

        book.setAuthor(authors);
        bookRepository.save(book);

        Inventory inventory = inventoryRepository.findById(1); // assuming one inventory

        // Assuming the book is successfully saved, create a new InventoryItem
        InventoryItem inventoryItem = new InventoryItem(book, 1, inventory); // Assuming the default quantity is 1
        inventoryItemRepository.save(inventoryItem); // Save the new inventory item

        inventory.addItemToInventory(inventoryItem);
        inventoryRepository.save(inventory);

        // Redirecting to homepage which should now include the new book
        return "redirect:/listAvailableBooks";
    }

    /**
     * Handler method to display the form for editing an existing book
     * @param isbn
     * @param model
     * @return Edit Book
     * @author Sabah Samwatin
     */
    @GetMapping("/edit/{isbn}")
    public String showEditForm(@PathVariable String isbn, Model model, HttpServletRequest request, HttpServletResponse response) {
        // Check if an owner is logged in
        BookUser loggedInUser = userController.getLoggedInUser(request.getCookies());
        if (loggedInUser == null || !loggedInUser.getUserType().equals(UserType.BOOKOWNER)) {
            return "access-denied";
        }

        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            // logic for editBook.html author
            String authors = book.getAuthor().stream()
                    .map(author -> author.getFirstName() + " " + author.getLastName())
                    .collect(Collectors.joining(", "));
            model.addAttribute("book", book);
            model.addAttribute("authorsList", authors);
            return "editBook";
        }
        return "redirect:/"; // if the book doesn't exist, redirect to the home page
    }

    /**
     * Handler method to process the edit form
     * @param book
     * @param authorsInput
     * @param model
     * @return Book Detail Page
     * @author Sabah Samwatin
     */
    @Transactional
    @PostMapping("/edit")
    public String handleEditForm(@ModelAttribute Book book, @RequestParam String authorsInput, Model model) {
        try {
            String[] authorNames = authorsInput.split(",");
            ArrayList<Author> authors = new ArrayList<>();

            for (String fullName : authorNames) {
                String[] parts = fullName.trim().split("\\s+");
                String firstName = parts[0];
                String lastName = (parts.length > 1) ? parts[1] : "";

                List<Author> foundAuthors = authorRepository.findByFirstNameAndLastName(firstName, lastName);
                Author author;
                if (foundAuthors.isEmpty()) {
                    author = new Author(firstName, lastName);
                    authorRepository.save(author);
                } else {
                    // Here we just take the first found author
                    author = foundAuthors.get(0);
                }
                authors.add(author);
            }
            book.setAuthor(authors);
            bookRepository.save(book); // save method will update if the book exists
            return "redirect:/book/info/" + book.getIsbn(); // redirect to the book detail page
        }
        catch (Exception e){
            log.error("Exception occurred while editing book: ", e);
            model.addAttribute("errorMessage", "An error occurred while saving the book.");
            return "editBook";
        }
    }

    /**
     * Handler method to display book details
     * @param isbn
     * @param model
     * @return Book info
     * @author Sabah Samwatin, Thanuja Sivaananthan
     */

    @GetMapping("/info/{isbn}")
    public String showBookDetails(@PathVariable String isbn, Model model, HttpServletRequest request, HttpServletResponse response) {
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

    // Add other handler methods as needed...
}
