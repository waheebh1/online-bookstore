/**
 * Test class for Inventory object
 * @author Maisha Abdullah
 */

package bookstore.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

public class InventoryTest {

    private Book book1;
    private Book book2;
    private InventoryItem item1;
    private InventoryItem item2;

    @BeforeEach
    public void setUp(){
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);

        String description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        this.book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
        this.item1 = new InventoryItem(book1, 5);

        ArrayList<Author> author_list2 = new ArrayList<>();
        Author author2 = new Author("Khaled", "Hosseini");
        author_list2.add(author2);
        String description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
        this.book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.00, "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
        this.item2 = new InventoryItem(book2, 10);
    }

    @Test
    public void InventoryCreation(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        assertEquals(0, inventory.getAvailableBooks().size());

        availableBooks.add(item1);
        availableBooks.add(item2);

        assertEquals(2, inventory.getAvailableBooks().size());
    }

    @Test
    public void testAddToInventory(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        //check that quantity should be positive and inventory is empty
        this.item1 = new InventoryItem(book1, -5);
        assertFalse(inventory.addItemToInventory(item1));
        assertEquals(0,inventory.getAvailableBooks().size());

        //check a first book has been added to inventory
        this.item1 = new InventoryItem(book1, 5);
        assertTrue(inventory.addItemToInventory(item1));
        assertEquals(1,inventory.getAvailableBooks().size());

        //check that book is updated in quantity and inventory size stays same
        this.item1 = new InventoryItem(book1, 3);
        assertTrue(inventory.addItemToInventory(item1));
        assertEquals(1,inventory.getAvailableBooks().size());
        assertEquals(8, inventory.getAvailableBooks().get(0).getQuantity());

        //check that a new book is added to inventory and inventory size increases
        this.item2 = new InventoryItem(book2, 3);
        assertTrue(inventory.addItemToInventory(item2));
        assertEquals(2,inventory.getAvailableBooks().size());
        assertEquals(3, inventory.getAvailableBooks().get(1).getQuantity());
    }

    @Test
    public void testReduceFromInventory(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        //check that quantity should be positive and inventory is empty
        assertFalse(inventory.reduceFromInventory(book1, -5));
        assertEquals(0,inventory.getAvailableBooks().size());

        //check method does not remove non-existent book
        assertFalse(inventory.reduceFromInventory(book1, 5));
        assertEquals(0,inventory.getAvailableBooks().size());

        this.item1 = new InventoryItem(book1, 5);
        inventory.addItemToInventory(item1);
        assertEquals(1,inventory.getAvailableBooks().size());

        //check method does not remove when quantity is greater than available quantity
        assertFalse(inventory.reduceFromInventory(book1, 8));
        assertEquals(1,inventory.getAvailableBooks().size());

        //check that book is removed from inventory by quantity
        assertTrue(inventory.reduceFromInventory(book1, 3));
        assertEquals(2, inventory.getAvailableBooks().get(0).getQuantity());

        //check that book is removed from inventory permanently
        assertTrue(inventory.reduceFromInventory(book1, 2));
        assertEquals(0,inventory.getAvailableBooks().size());
    }
}
