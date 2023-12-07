package bookstore.inventory;

import bookstore.mockservlet.MockHttpServletRequest;
import bookstore.mockservlet.MockHttpServletResponse;
import bookstore.users.BookUser;
import bookstore.users.UserController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * ViewInventoryController Test
 * @author Waheeb Hashmi
 * @author Thanuja Sivaananthan
 */
@SpringBootTest
public class ViewInventoryControllerTest {

    @Mock
    private UserController userController;

    @InjectMocks
    private CheckoutController controller;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;
    private Inventory inventory;


    /**
     * Method to set up the books and inventory before each test method
     * @author Waheeb Hashmi
     */
    @BeforeEach
    void setUp(){
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);

        String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
        InventoryItem item1 = new InventoryItem(book1, 5);

        ArrayList<Author> author_list2 = new ArrayList<>();
        Author author2 = new Author("Khaled", "Hosseini");
        author_list2.add(author2);
        String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
        book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.00, "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
        InventoryItem item2 = new InventoryItem(book2, 10);

        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        inventory = new Inventory(availableBooks);
        inventory.addItemToInventory(item1);
        inventory.addItemToInventory(item2);

        bookRepository.save(book1);
        bookRepository.save(book2);
        inventoryRepository.save(inventory);
    }

    /**
     * Test method to ensure that controller loads properly
     * @author Waheeb Hashmi
     */
    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    /**
     * Get default filter values
     * @return list containing: author list, genre list, publisher list
     * @author Thanuja Sivaananthan
     */
    public List<List<String>> getDefaultFilterValues(){
        List<String> authorList = new ArrayList<>();
        authorList.add(book1.getAuthor().get(0).getFullName());
        authorList.add(book2.getAuthor().get(0).getFullName());


        List<String> genreList = new ArrayList<>();
        genreList.add(book1.getGenre());
        genreList.add(book2.getGenre());

        List<String> publisherList = new ArrayList<>();
        publisherList.add(book1.getPublisher());
        publisherList.add(book2.getPublisher());

        List<List<String>> defaultFilters = new ArrayList<>();
        defaultFilters.add(authorList);
        defaultFilters.add(genreList);
        defaultFilters.add(publisherList);

        return defaultFilters;
    }

    /**
     * Test method to view whole inventory
     * @author Thanuja Sivaananthan
     */
    @Test
    void testViewAvailableBooks(){
        String searchValue = "";

        List<List<String>> defaultFilters = getDefaultFilterValues();

        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response, searchValue, "low_to_high", defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), null, model);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(inventory.getAvailableBooks(), model.getAttribute("inventoryItems"));
    }

    /**
     * Test method to view whole inventory
     * @author Thanuja Sivaananthan
     */
    @Test
    void testViewAvailableBooksWithoutAccess(){
        String searchValue = "";

        List<List<String>> defaultFilters = getDefaultFilterValues();

        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(null);

        String view = controller.listAvailableBooks(request, response, searchValue, "low_to_high", defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), null, model);

        Assertions.assertEquals("access-denied", view);
    }

    /**
     * Test method to view inventory
     * @author Thanuja Sivaananthan
     */
    @Test
    void testViewAvailableBooksWithVagueSearch(){
        String searchValue = "the";

        List<List<String>> defaultFilters = getDefaultFilterValues();

        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response, searchValue, "low_to_high", defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), null, model);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(inventory.getAvailableBooks(), model.getAttribute("inventoryItems"));
    }

    /**
     * Test method to view inventory
     * @author Thanuja Sivaananthan
     */
    @Test
    void testViewAvailableBooksWithTitleSearch(){
        String searchValue = "mockingbird";

        List<List<String>> defaultFilters = getDefaultFilterValues();

        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response, searchValue, "low_to_high", defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), null, model);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(Collections.singletonList(inventory.getAvailableBooks().get(0)), model.getAttribute("inventoryItems"));
    }

}
