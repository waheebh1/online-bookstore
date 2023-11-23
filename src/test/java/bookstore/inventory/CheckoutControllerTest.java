package bookstore.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import bookstore.users.UserController;

import java.util.ArrayList;


/**
 * CheckoutController Test
 * @author Waheeb Hashmi
 */
@SpringBootTest
class CheckoutControllerTest {

    @Mock
    private UserController userController;
    
    @InjectMocks
    private CheckoutController controller;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private InventoryRepository inventoryRepository;

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
     * Test method to ensure that the confirmOrder() method shows the correct page and deletes shopping cart repo
     * @author Waheeb Hashmi
     */
    @Test
    void testConfirmOrder() {
        Model model = new ConcurrentModel();

        String view = controller.confirmOrder(model);

        Assertions.assertEquals("order-confirmation", view);
        Assertions.assertTrue(model.containsAttribute("confirmationNumber"));
        verify(shoppingCartRepository).deleteAll();
    }

    /**
     * Test method to view the checkout page (with items) for a user that has access
     * @author Waheeb Hashmi
     */
    @Test
    void testViewCartWithoutAccess(){
        when(userController.getUserAccess()).thenReturn(false);
    
        Model model = new ConcurrentModel();
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 3);
        shoppingCart.addToCart(book2, 1);

        String view = controller.viewCart(model);
        model.addAttribute("items", shoppingCart.getBooksInCart());

        Assertions.assertEquals("access-denied", view);
        Assertions.assertEquals(shoppingCart.getBooksInCart(), model.getAttribute("items"));
        Assertions.assertTrue(model.containsAttribute("totalPrice"));

    }
    
    /**
     * Test method to view the checkout page (with items) for a user that does not have access
     * @author Waheeb Hashmi
     */
    @Test
    void testViewCartWithAccess(){
        when(userController.getUserAccess()).thenReturn(true);

        Model model = new ConcurrentModel();
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 3);
        shoppingCart.addToCart(book2, 1);

        String view = controller.viewCart(model);
        model.addAttribute("items", shoppingCart.getBooksInCart());

        Assertions.assertEquals("checkout", view);
        Assertions.assertEquals(shoppingCart.getBooksInCart(), model.getAttribute("items"));
        Assertions.assertTrue(model.containsAttribute("totalPrice"));
    }
}