/**
 * Test class for Book object
 * @author Shrimei Chock
 */

package bookstore.inventory;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;

public class BookTest {
    @Test
    public void testBookCreation(){
        ArrayList<Author> author_list = new ArrayList<>();
        Author author1 = new Author("Harper", "Lee");
        author_list.add(author1);
        String description = "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior - to innocence and experience, kindness and cruelty, love and hatred, humor and pathos.";
        Book book1 = new Book("0446310786", "To Kill a Mockingbird", author_list, 12.99, "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", "Grand Central Publishing", "Classical", description);

        assertEquals("0446310786", book1.getIsbn());
        assertEquals("To Kill a Mockingbird", book1.getTitle());
        assertEquals("https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg", book1.getPicture());
        assertEquals(1, book1.getAuthor().size());
        for (Author author : book1.getAuthor()){
            assertEquals("Harper", author.getFirstName());
            assertEquals("Lee", author.getLastName());
        }
        assertEquals("Grand Central Publishing", book1.getPublisher());
        assertEquals("Classical", book1.getGenre());
        assertEquals(12.99, book1.getPrice(), 0.001);
        assertEquals(description, book1.getDescription());
    }
}
