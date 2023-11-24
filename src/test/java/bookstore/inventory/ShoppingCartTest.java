package bookstore.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ShoppingCart Tests
 * @author Maisha Abdullah
 * @author Thanuja Sivaananthan
 */
public class ShoppingCartTest {

    private Book book1;
    private Book book2;
    private Book book3;
    private Inventory inventory;
    private final int PRICE_DELTA = 0;

    @BeforeEach
    public void setUp(){
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);

        String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        this.book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
        InventoryItem item1 = new InventoryItem(book1, 15);

        ArrayList<Author> author_list2 = new ArrayList<>();
        Author author2 = new Author("Khaled", "Hosseini");
        author_list2.add(author2);
        String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
        this.book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.00, "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
        InventoryItem item2 = new InventoryItem(book2, 10);

        //BOOK NOT IN INVENTORY
        ArrayList<Author> author_list3 = new ArrayList<>();
        Author author3 = new Author("X", "X");
        author_list3.add(author3);
        String description3 = "X";
        this.book3 = new Book("X", "X", author_list3, 22.00,"X", "X", "X", "X", description3);

        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        inventory = new Inventory(availableBooks);
        inventory.addItemToInventory(item1);
        inventory.addItemToInventory(item2);
    }

    @Test
    public void shoppingCartCreation(){
        ShoppingCart shoppingCart = new ShoppingCart(inventory);

        assertEquals(0,shoppingCart.getTotalPrice(), PRICE_DELTA);
    }

    @Test
    public void testAddToCart(){
        ShoppingCart shoppingCart = new ShoppingCart(inventory);

        //check that quantity should be positive and cart is empty
        assertFalse(shoppingCart.addToCart(book1, -3));
        assertTrue(shoppingCart.getBooksInCart().isEmpty());

        //add book to cart
        assertTrue(shoppingCart.addToCart(book1, 10));
        assertEquals(1, shoppingCart.getBooksInCart().size());
        assertEquals(10, shoppingCart.getBooksInCart().get(0).getQuantity());
        assertEquals(129.9, shoppingCart.getTotalPrice(), PRICE_DELTA);

        //update book in cart
        assertTrue(shoppingCart.addToCart(book1, 3));
        assertEquals(1, shoppingCart.getBooksInCart().size());
        assertEquals(13, shoppingCart.getBooksInCart().get(0).getQuantity());
        assertEquals(168.87, shoppingCart.getTotalPrice(), PRICE_DELTA);

        //add new book to cart
        assertTrue(shoppingCart.addToCart(book2, 4));
        assertEquals(2, shoppingCart.getBooksInCart().size());
        assertEquals(4, shoppingCart.getBooksInCart().get(1).getQuantity());
        assertEquals(256.87, shoppingCart.getTotalPrice(), PRICE_DELTA);

        //add nonexistent book in inventory to cart
        assertFalse(shoppingCart.addToCart(book3, 3));
        assertEquals(2, shoppingCart.getBooksInCart().size());

        //update existing book in cart when length of cart is greater than 1
        assertTrue(shoppingCart.addToCart(book2, 2));
        assertEquals(2, shoppingCart.getBooksInCart().size());
        assertEquals(13, shoppingCart.getBooksInCart().get(0).getQuantity());
        assertEquals(6, shoppingCart.getBooksInCart().get(1).getQuantity());
        assertEquals(300.87, shoppingCart.getTotalPrice(), PRICE_DELTA);
    }

    @Test
    public void testRemoveFromCart(){
        ShoppingCart shoppingCart = new ShoppingCart(inventory);

        //check that quantity should be positive and cart is empty
        assertFalse(shoppingCart.removeFromCart(book1, -3));
        assertTrue(shoppingCart.getBooksInCart().isEmpty());

        shoppingCart.addToCart(book1, 10);

        //check a larger amount of quantity cannot be removed than the available quantity
        assertFalse(shoppingCart.removeFromCart(book1, 11));
        assertEquals(1, shoppingCart.getBooksInCart().size());
        assertEquals(129.9, shoppingCart.getTotalPrice(), PRICE_DELTA);

        //check that a nonexistent book cannot be removed from cart
        assertFalse(shoppingCart.removeFromCart(book2, 3));
        assertEquals(1, shoppingCart.getBooksInCart().size());

        //check that books can be removed by quantity when size = 1
        assertTrue(shoppingCart.removeFromCart(book1, 9));
        assertEquals(1, shoppingCart.getBooksInCart().get(0).getQuantity());
        assertEquals(12.99, shoppingCart.getTotalPrice(), PRICE_DELTA);

        //add new item to increase length of cart
        shoppingCart.addToCart(book2, 10);
        assertTrue(shoppingCart.removeFromCart(book2, 9));
        assertEquals(2, shoppingCart.getBooksInCart().size());
        assertEquals(1, shoppingCart.getBooksInCart().get(1).getQuantity());
        assertEquals(34.99, shoppingCart.getTotalPrice(), PRICE_DELTA);

        //check that books can be removed permanently
        assertTrue(shoppingCart.removeFromCart(book1, 1));
        assertEquals(1, shoppingCart.getBooksInCart().size());
        assertEquals(22.00, shoppingCart.getTotalPrice(), PRICE_DELTA);
    }

    @Test
    public void testCheckout(){
        ShoppingCart shoppingCart = new ShoppingCart(inventory);
        shoppingCart.addToCart(book1, 10);
        shoppingCart.addToCart(book2, 10);

        assertEquals(2, shoppingCart.getBooksInCart().size());

        shoppingCart.checkout();

        assertTrue(shoppingCart.getBooksInCart().isEmpty());
    }
}
