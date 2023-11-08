/**
 * Repository for Book objects
 * @author Shrimei Chock
 */
package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface BookRepository extends CrudRepository<Book, String>{
    Book findByIsbn(String isbn);
    ArrayList<Book> findByTitle(String title);
}
