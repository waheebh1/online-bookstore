package bookstore.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;

import java.util.Optional;

import bookstore.mockservlet.MockHttpServletRequest;
import bookstore.mockservlet.MockHttpServletResponse;
import bookstore.users.BookUser;
import bookstore.users.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import bookstore.users.UserController;

import java.util.ArrayList;
import java.util.Collections;


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

    @Mock
    private ShoppingCart shoppingCart;

    @Mock
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private Book book1;
    private Book book2;
    private Inventory inventory;
    private InventoryItem invItem1;


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
        BookUser bookUser = new BookUser("testUser", "password123");
        bookUser.setShoppingCart(shoppingCart);

        Model model = new ConcurrentModel();

        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);
        String view = controller.confirmOrder(request, response, model);

        // Verify that the shopping cart is checked out
        verify(shoppingCart).checkout();

        Assertions.assertEquals("order-confirmation", view);
        Assertions.assertTrue(model.containsAttribute("confirmationNumber"));

        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartItemRepository).saveAll(shoppingCart.getBooksInCart());
        verify(inventoryRepository).save(inventoryRepository.findById(1));

    }

    /**
     * Test method to view the checkout page (with items) for a user that has access
     * @author Waheeb Hashmi
     */
    @Test
    void testViewCartWithoutAccess(){
        when(userController.getUserAccess()).thenReturn(false);

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 3);
        shoppingCart.addToCart(book2, 1);

        String view = controller.viewCart(request, response, model);
        model.addAttribute("items", shoppingCart.getBooksInCart());

        Assertions.assertEquals("access-denied", view);
        Assertions.assertEquals(shoppingCart.getBooksInCart(), model.getAttribute("items"));
        Assertions.assertFalse(model.containsAttribute("totalPrice"));

    }

    /**
     * Test method to view the checkout page (with items) for a user that does not have access
     * @author Waheeb Hashmi
     */
    @Test
    void testViewCartWithAccess(){
        when(userController.getUserAccess()).thenReturn(true);

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 3);
        shoppingCart.addToCart(book2, 1);

        BookUser bookUser = new BookUser("testUser", "password123");
        bookUser.setShoppingCart(shoppingCart);
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        String view = controller.viewCart(request, response, model);
        model.addAttribute("items", shoppingCart.getBooksInCart());

        Assertions.assertEquals("checkout", view);
        Assertions.assertEquals(shoppingCart.getBooksInCart(), model.getAttribute("items"));
        Assertions.assertTrue(model.containsAttribute("totalPrice"));
    }


    /**
     * Test method to add to cart
     * @author Maisha Abdullah
     */
    @Test
    void testAddToCart() {
        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        // Create an inventory item
        InventoryItem invItem1 = new InventoryItem(book1, 5, inventory);
        invItem1.setId(1L);

        // Create a shopping cart
        ShoppingCart shoppingCart = new ShoppingCart(inventory);

        // Add an item to the shopping cart
        shoppingCart.addToCart(book1, 1);

        BookUser bookUser = new BookUser("testUser", "password123");
        bookUser.setShoppingCart(shoppingCart);
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        when(inventoryItemRepository.findById(1)).thenReturn(invItem1);

        String[] selectedItems = new String[]{"1"};
        String view = controller.addToCart(request, response, selectedItems, model);

        model.addAttribute("items", shoppingCart.getBooksInCart());
        model.addAttribute("quantity", inventory.findAvailableBook("0446310786").getQuantity());

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(shoppingCart.getBooksInCart(), model.getAttribute("items"));
        Assertions.assertEquals(inventory.findAvailableBook("0446310786").getQuantity(), model.getAttribute("quantity"));

        verify(inventoryItemRepository).save(invItem1);
        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartItemRepository).saveAll(shoppingCart.getBooksInCart());
    }

    /**
     * Test method to remove from cart
     * @author Maisha Abdullah
     */
    @Test
    void testRemoveFromCart() {
        when(userController.getUserAccess()).thenReturn(true);
        when(inventoryRepository.findById(1)).thenReturn(inventory);

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();


        // Create an inventory item
        InventoryItem invItem1 = new InventoryItem(book1, 5, inventory);
        invItem1.setId(1L);

        // Create a shopping cart
        ShoppingCart shoppingCart = new ShoppingCart(inventory);

        // Add an item to the shopping cart then remove
        shoppingCart.addToCart(book1, 1);
        shoppingCart.removeFromCart(book1, 1);

        BookUser bookUser = new BookUser("testUser", "password123");
        bookUser.setShoppingCart(shoppingCart);
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookUser);

        when(inventoryItemRepository.findById(1)).thenReturn(invItem1);

        String[] selectedItems = new String[]{"1"};
        String view = controller.removeFromCart(request, response, selectedItems, model);

        model.addAttribute("items", shoppingCart.getBooksInCart());
        model.addAttribute("quantity", inventory.findAvailableBook("0446310786").getQuantity());

        Assertions.assertEquals("home", view);
        Assertions.assertEquals(shoppingCart.getBooksInCart(), model.getAttribute("items"));
        Assertions.assertEquals(inventory.findAvailableBook("0446310786").getQuantity(), model.getAttribute("quantity"));

        verify(inventoryItemRepository).save(invItem1);
        verify(shoppingCartRepository).save(shoppingCart);
        verify(shoppingCartItemRepository).saveAll(shoppingCart.getBooksInCart());
    }



}