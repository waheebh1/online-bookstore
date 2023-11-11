/**
 * Repository for Book objects
 * @author Shrimei Chock
 */
package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;

public interface BookRepository extends CrudRepository<Book, String>{
    Book findBIsbn(String isbn);
    List<Book> findByTitle(String title);
    List<Book> findByGenre(String genre);
    List<Book> findByAuthor(ArrayList<Author> author);
}
