/**
 * Test class for Author object
 * @author Shrimei Chock
 */

package bookstore.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class AuthorTest {
    @Test
    public void testAuthorCreation(){
        Author author1 = new Author("Mary", "Jones");
        assertEquals("Mary", author1.getFirstName());
        assertEquals("Jones", author1.getLastName());
    }

    @Test
    public void testBibliography(){
        Author author2 = new Author("Elenor", "Tatem");
        assertEquals(0, author2.getBibliography().size());

        Book book1 = new Book();
        Book book2 = new Book();
        author2.addBook(book1);
        author2.addBook(book2);
        assertEquals(2, author2.getBibliography().size());

        author2.removeBook(book1);
        assertEquals(1, author2.getBibliography().size());
    }

}
