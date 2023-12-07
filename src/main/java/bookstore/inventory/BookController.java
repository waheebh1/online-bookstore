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
    public String handleUploadForm(@ModelAttribute Book book, @RequestParam String authorsInput, @RequestParam int quantity, Model model) {
        // Check for existing book with the same ISBN
        Book existingBook = bookRepository.findByIsbn(book.getIsbn());
        if (existingBook != null) {
            model.addAttribute("isbnErrorMessage", "ISBN should be unique, \nExisting ISBN for " + existingBook.getIsbn() + ": " + existingBook.getTitle());
            return "uploadBook";
        }
        String[] authorNames = authorsInput.split(",");
        ArrayList<Author> authors = new ArrayList<>();

        for (String fullName : authorNames) {
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length < 2) {
                model.addAttribute("authorErrorMessage", "Each author must have both a first name and a last name.");
                model.addAttribute("book", book);
                return "uploadBook";
            }
            String firstName = parts[0];
            String lastName = parts[1]; // Taking the second part as the last name, assuming no middle name

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
        InventoryItem inventoryItem = new InventoryItem(book, quantity, inventory); // Assuming the default quantity is 1
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
            // Fetch the quantity from the InventoryItem
            List<InventoryItem> inventoryItems = inventoryItemRepository.findByBook(book);
            int bookQuantity = (inventoryItems.isEmpty()) ? 0 : inventoryItems.get(0).getQuantity();
            model.addAttribute("book", book);
            model.addAttribute("authorsList", authors);
            model.addAttribute("quantity", bookQuantity); // Add the quantity to the model here
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
    public String handleEditForm(@ModelAttribute Book book, @RequestParam String authorsInput, @RequestParam int quantity, Model model) {
        try {
            String[] authorNames = authorsInput.split(",");
            ArrayList<Author> authors = new ArrayList<>();

            for (String fullName : authorNames) {
                String[] parts = fullName.trim().split("\\s+");
                if (parts.length < 2) {
                    model.addAttribute("authorErrorMessage", "Each author must have both a first name and a last name.");
                    model.addAttribute("book", book);
                    return "editBook";
                }
                String firstName = parts[0];
                String lastName = parts[1]; // Taking the second part as the last name, assuming no middle name
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

            List<InventoryItem> inventoryItems = inventoryItemRepository.findByBook(book);
            if (!inventoryItems.isEmpty()) {
                InventoryItem inventoryItem = inventoryItems.get(0); // Get the first item
                inventoryItem.setQuantity(quantity); // Update quantity
                inventoryItemRepository.save(inventoryItem); // Save the updated item
            }
            return "redirect:/viewBook?isbn=" + book.getIsbn();        }
        catch (Exception e){
            log.error("Exception occurred while editing book: ", e);
            model.addAttribute("errorMessage", "An error occurred while saving the book.");
            return "editBook";
        }
    }

    // Add other handler methods as needed...
}
