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

import static java.util.Collections.max;
import static org.mockito.Mockito.when;

/**
 * ViewInventoryController Test
 * @author Shrimei Chock
 */
@SpringBootTest
public class SortFilterTest {

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
    private Book book3;
    private InventoryItem item1;
    private InventoryItem item2;
    private InventoryItem item3;
    private Inventory inventory;
    String max_price;


    /**
     * Set up the books and inventory before each test method
     *
     * @author Shrimei Chock
     */
    @BeforeEach
    void setUp() {
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);

        String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
        item1 = new InventoryItem(book1, 5);

        ArrayList<Author> author_list2 = new ArrayList<>();
        Author author2 = new Author("Khaled", "Hosseini");
        author_list2.add(author2);
        String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
        book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.98, "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
        item2 = new InventoryItem(book2, 10);

        String description3 = "Twenty-six-year-old Jean Louise Finch returns home to Maycomb, Alabama from New York City to visit her aging father, Atticus.";
        book3 = new Book("978-0-06-240985-0", "Go Set a Watchman", author_list, 14.99, "14/07/2015", "https://m.media-amazon.com/images/I/61lFywIUwzL.jpg", "Harper Collins", "Historical fiction", description3);
        item3 = new InventoryItem(book3, 2, inventory);

        max_price = String.valueOf(Math.max(book1.getPrice(), Math.max(book2.getPrice(), book3.getPrice())));

        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        inventory = new Inventory(availableBooks);
        inventory.addItemToInventory(item1);
        inventory.addItemToInventory(item2);
        inventory.addItemToInventory(item3);

        bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
        inventoryRepository.save(inventory);
    }

    /**
     * Get all possible filter values for the books in inventory
     *
     * @return list containing: author list, genre list, publisher list
     * @author Thanuja Sivaananthan
     * @author Shrimei Chock
     */
    public List<List<String>> getDefaultFilterValues() {
        List<String> authorList = new ArrayList<>();
        authorList.add(book1.getAuthor().get(0).getFullName());
        authorList.add(book2.getAuthor().get(0).getFullName());
        authorList.add(book3.getAuthor().get(0).getFullName());

        List<String> genreList = new ArrayList<>();
        genreList.add(book1.getGenre());
        genreList.add(book2.getGenre());
        genreList.add(book3.getGenre());

        List<String> publisherList = new ArrayList<>();
        publisherList.add(book1.getPublisher());
        publisherList.add(book2.getPublisher());
        publisherList.add(book3.getPublisher());

        List<List<String>> defaultFilters = new ArrayList<>();
        defaultFilters.add(authorList);
        defaultFilters.add(genreList);
        defaultFilters.add(publisherList);

        return defaultFilters;
    }

    /**
     * View inventory for all sort options
     * @author Shrimei Chock
     */
    @Test
    void testViewAvailableBooksWithSort(){

        List<List<String>> defaultFilters = getDefaultFilterValues();

        //give user access and set inventory to the main inventory
        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        //call controller endpoint directly
        //Test low_to_high
        String filter_option = SortCriteria.LOW_TO_HIGH.label;
        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response, "", filter_option, defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), max_price, model);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(inventory.getAvailableBooks(), model.getAttribute("inventoryItems"));

        //Test high_to_low
        filter_option = SortCriteria.HIGH_TO_LOW.label;
        view = controller.listAvailableBooks(request, response, "", filter_option, defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), max_price, model);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(inventory.getAvailableBooks(), model.getAttribute("inventoryItems"));

        //Test alphabetical
        filter_option = SortCriteria.ALPHABETICAL.label;
        view = controller.listAvailableBooks(request, response, "", filter_option, defaultFilters.get(0), defaultFilters.get(1), defaultFilters.get(2), max_price, model);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(inventory.getAvailableBooks(), model.getAttribute("inventoryItems"));
    }

    /**
     * View inventory for all filter options
     * @author Shrimei Chock
     */
    @Test
    void testViewAvailableBooksWithFilter(){
        String filter_option = SortCriteria.LOW_TO_HIGH.label;
        List<List<String>> defaultFilters = getDefaultFilterValues();

        //give user access and set inventory to the main inventory
        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        //Change filters for author
        List<String> authorList = new ArrayList<>();
        authorList.add(book1.getAuthor().get(0).getFullName());

        //call controller endpoint directly
        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response, "", filter_option, authorList, defaultFilters.get(1), defaultFilters.get(2), max_price, model);

        //hardcode expected value
        ArrayList<InventoryItem> expected = new ArrayList<>();
        expected.add(item1);
        expected.add(item3);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(expected, model.getAttribute("inventoryItems"));

        //Change filters for genre
        List<String> genreList = new ArrayList<>();
        genreList.add(book2.getGenre());

        view = controller.listAvailableBooks(request, response, "", filter_option, defaultFilters.get(0), genreList, defaultFilters.get(2), max_price, model);

        //hardcode expected value
        expected = new ArrayList<>();
        expected.add(item3);
        expected.add(item2); //order for low_to_high

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(expected, model.getAttribute("inventoryItems"));

        //Change filters for publisher
        List<String> publisherList = new ArrayList<>();
        publisherList.add(book3.getPublisher());

        view = controller.listAvailableBooks(request, response, "", filter_option, defaultFilters.get(0), defaultFilters.get(1), publisherList, max_price, model);

        //hardcode expected value
        expected = new ArrayList<>();
        expected.add(item3);

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(expected, model.getAttribute("inventoryItems"));
    }

    /**
     * Test non-default sort and filter options combined
     * @author Shrimei Chock
     */
    @Test
    void testViewAvailableBooksWithSortFilter(){
        List<List<String>> defaultFilters = getDefaultFilterValues();

        //high_to_low with genre filter
        String filter_option = SortCriteria.HIGH_TO_LOW.label;
        List<String> genreList = new ArrayList<>();
        genreList.add(book2.getGenre());

        //give user access and set inventory to the main inventory
        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        //call controller endpoint directly
        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response,"", filter_option, defaultFilters.get(0), genreList, defaultFilters.get(2), max_price, model);

        //hardcode expected value
        ArrayList<InventoryItem> expected = new ArrayList<>();
        expected.add(item2);
        expected.add(item3); //order for high_to_low

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(expected, model.getAttribute("inventoryItems"));
    }

    /**
     * Test restrictive filter criteria, no books match
     * @author Shrimei Chock
     */
    @Test
    void testViewAvailableBooksRestrictive(){
        List<List<String>> defaultFilters = getDefaultFilterValues();

        //no books matching the selected publisher and author
        String filter_option = SortCriteria.LOW_TO_HIGH.label;
        List<String> publisherList = new ArrayList<>();
        publisherList.add(book1.getGenre());
        List<String> authorList = new ArrayList<>();
        authorList.add(book2.getGenre());

        //give user access and set inventory to the main inventory
        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        //call controller endpoint directly
        Model model = new ConcurrentModel();
        BookUser bookUser = new BookUser("testUser", "password123");
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.listAvailableBooks(request, response,"", filter_option, authorList, defaultFilters.get(0), publisherList, max_price, model);

        //expecting no books to show up
        ArrayList<InventoryItem> expected = new ArrayList<>();

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(expected, model.getAttribute("inventoryItems"));
    }
}
