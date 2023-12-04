package bookstore.inventory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/book")
public class BookController {

    private final BookRepository bookRepository;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Autowired
    private AuthorRepository authorRepository;

    // Handler method to display the form for uploading a new book
    @GetMapping("/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("book", new Book());
        return "uploadBook";
    }

    // Handler method to process the upload form
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

        // Assuming the book is successfully saved, create a new InventoryItem
        InventoryItem inventoryItem = new InventoryItem(book, 1); // Assuming the default quantity is 1
        inventoryItemRepository.save(inventoryItem); // Save the new inventory item

        // Redirecting to homepage which should now include the new book
        return "redirect:/home";
    }

    // Handler method to display the form for editing an existing book
    @GetMapping("/edit/{isbn}")
    public String showEditForm(@PathVariable String isbn, Model model) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            model.addAttribute("book", book);
            return "editBook";
        }
        return "redirect:/"; // if the book doesn't exist, redirect to the home page
    }

    // Handler method to process the edit form
    @PostMapping("/edit")
    public String handleEditForm(@ModelAttribute Book book) {
        bookRepository.save(book); // save method will update if the book exists
        return "redirect:/book/info/" + book.getIsbn(); // redirect to the book detail page
    }

    // Handler method to display book details
    @GetMapping("/info/{isbn}")
    public String showBookDetails(@PathVariable String isbn, Model model) {
        Book book = bookRepository.findByIsbn(isbn);
        if (book != null) {
            model.addAttribute("book", book);
            return "book-info";
        }
        return "redirect:/"; // if the book doesn't exist, redirect to the home page
    }

    // Add other handler methods as needed...
}
