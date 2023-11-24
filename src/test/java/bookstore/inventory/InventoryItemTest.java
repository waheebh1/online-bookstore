/**
 * Test class for Inventory Item object
 * @author Maisha Abdullah
 */

package bookstore.inventory;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

public class InventoryItemTest {

    private Book book2;
    private InventoryItem item1;
    private String description1;
    private String description2;

    @BeforeEach
    public void setUp(){
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);

        this.description1 = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        Book book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "11/07/1960", "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description1);
        this.item1 = new InventoryItem(book1, 5);

        ArrayList<Author> author_list2 = new ArrayList<>();
        Author author2 = new Author("Khaled", "Hosseini");
        author_list2.add(author2);
        this.description2 = "The Kite Runner tells the story of Amir, a young boy from the Wazir Akbar Khan district of Kabul";
        this.book2 = new Book("1573222453", "The Kite Runner", author_list2, 22.00, "29/05/2003", "https://upload.wikimedia.org/wikipedia/en/6/62/Kite_runner.jpg", "Riverhead Books", "Historical fiction", description2);
    }

    @Test
    public void testInventoryItemCreation(){
        assertEquals("0446310786", item1.getBook().getIsbn());
        assertEquals(description1, item1.getBook().getDescription());
        assertEquals("Classical", item1.getBook().getGenre());
        assertEquals(5, item1.getQuantity());
    }

    @Test
    public void testSetBook(){
        item1.setBook(book2);

        assertEquals("1573222453", item1.getBook().getIsbn());
        assertEquals(description2, item1.getBook().getDescription());
        assertEquals("Historical fiction", item1.getBook().getGenre());
    }

    @Test
    public void testSetQuantity(){
        item1.setQuantity(10);

        assertEquals(10,item1.getQuantity());
    }
}
