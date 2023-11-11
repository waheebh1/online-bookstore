/**
 * Repository for Author objects
 * @author Shrimei Chock
 */
package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface AuthorRepository extends CrudRepository<Author, Long>{
    ArrayList<Author> findByFirstName(String firstName);
    ArrayList<Author> findByLastName(String lastName);
    Author findById(long id);
}
