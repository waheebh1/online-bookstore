package bookstore.inventory;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class InventorySearchTest {
    private Book book1;
    private Book book2;
    private InventoryItem item1;
    private InventoryItem item2;

    @Before
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



    /**
     * Test book search
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testEmptySearchBook(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        List<InventoryItem> searchedBooks;
        Inventory inventory = new Inventory(availableBooks);

        searchedBooks = inventory.getBooksMatchingSearch("");
        assertEquals(inventory.getAvailableBooks(), searchedBooks);

        availableBooks.add(item1);
        availableBooks.add(item2);

        searchedBooks = inventory.getBooksMatchingSearch("");
        assertEquals(inventory.getAvailableBooks(), searchedBooks);
    }

    /**
     * Test book search
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testSearchBookByTitle(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        availableBooks.add(item1);
        availableBooks.add(item2);

        List<InventoryItem> searchedBooks = inventory.getBooksMatchingSearch("kite");
        assertEquals(1, searchedBooks.size());
        assertEquals(item2, searchedBooks.get(0));
    }

    /**
     * Test book search
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testSearchBookByAuthor(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        availableBooks.add(item1);
        availableBooks.add(item2);

        List<InventoryItem> searchedBooks = inventory.getBooksMatchingSearch("harper");
        assertEquals(1, searchedBooks.size());
        assertEquals(item1, searchedBooks.get(0));
    }

    /**
     * Test book search
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testSearchBookByDescription(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        availableBooks.add(item1);
        availableBooks.add(item2);

        List<InventoryItem> searchedBooks = inventory.getBooksMatchingSearch("amir");
        assertEquals(1, searchedBooks.size());
        assertEquals(item2, searchedBooks.get(0));
    }

    /**
     * Test book search - title should come before description
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testSearchBookOrder1(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        availableBooks.add(item1);
        availableBooks.add(item2);

        List<InventoryItem> searchedBooks = inventory.getBooksMatchingSearch("to");
        assertEquals(2, searchedBooks.size());
        assertEquals(item1, searchedBooks.get(0));
        assertEquals(item2, searchedBooks.get(1));
    }

    /**
     * Test book search - title should come before description
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testSearchBookOrder2(){
        ArrayList<InventoryItem> availableBooks = new ArrayList<>();
        Inventory inventory = new Inventory(availableBooks);

        availableBooks.add(item1);
        availableBooks.add(item2);

        List<InventoryItem> searchedBooks = inventory.getBooksMatchingSearch("the");
        assertEquals(2, searchedBooks.size());
        assertEquals(item2, searchedBooks.get(0));
        assertEquals(item1, searchedBooks.get(1));
    }
}
