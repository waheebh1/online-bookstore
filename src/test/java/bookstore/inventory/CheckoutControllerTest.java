package bookstore.inventory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bookstore.mockservlet.MockHttpServletRequest;
import bookstore.mockservlet.MockHttpServletResponse;
import bookstore.users.BookUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import bookstore.users.UserController;
import bookstore.users.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


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
    private UserRepository userRepository;

    @Mock
    private ShoppingCart shoppingCart;

    @Mock
    private ShoppingCartItemRepository shoppingCartItemRepository;

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
        // verify(shoppingCartItemRepository).saveAll(shoppingCart.getBooksInCart());
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
     * Test method to get recommended books
     * @author Waheeb Hashmi
     */
    @Test
    void testGetRecommendedBooks(){
        long userId = 1;
        long otherUserId = 2; 
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 1);
        BookUser mockUser = new BookUser("testUser", "password123");
        mockUser.setId(userId); 
        mockUser.setShoppingCart(shoppingCart);
    
        ShoppingCart otherUserCart = new ShoppingCart(inventory);
        otherUserCart.addToCart(book2, 1); 
        BookUser otherUser = new BookUser("otherUser", "password123");
        otherUser.setId(otherUserId);
        otherUser.setShoppingCart(otherUserCart);
    
        Mockito.when(userRepository.findById(userId)).thenReturn(mockUser);
        Mockito.when(userRepository.findById(otherUserId)).thenReturn(otherUser);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(mockUser, otherUser));
        otherUser.getShoppingCart().checkout();
        ArrayList<Book> recommendedBooks = controller.recommendBooks(userId);
    
        Assertions.assertEquals(1, recommendedBooks.size());
        Assertions.assertTrue(recommendedBooks.contains(book2));
    
    }
    
     /**
     * Test method to get books in cart by userid
     * @author Waheeb Hashmi
     */
    @Test
    void testGetBooksByUserId(){
        long userId = 1;
        long otherUserId = 2; 
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 1);
        BookUser mockUser = new BookUser("testUser", "password123");
        mockUser.setId(userId); 
        mockUser.setShoppingCart(shoppingCart);
    
        ShoppingCart otherUserCart = new ShoppingCart(inventory);
        otherUserCart.addToCart(book2, 1); 
        BookUser otherUser = new BookUser("otherUser", "password123");
        otherUser.setId(otherUserId);
        otherUser.setShoppingCart(otherUserCart);
    
        Mockito.when(userRepository.findById(userId)).thenReturn(mockUser);
        Mockito.when(userRepository.findById(otherUserId)).thenReturn(otherUser);
        Mockito.when(userRepository.findAll()).thenReturn(Arrays.asList(mockUser, otherUser));
        mockUser.getShoppingCart().checkout();

        Set<Book> recommendedBooks = controller.getBooksInCartByUserId(userId);
    
        Assertions.assertEquals(1, recommendedBooks.size()); 
        Assertions.assertFalse(recommendedBooks.contains(book2)); 
    
    
    }
    
    
}