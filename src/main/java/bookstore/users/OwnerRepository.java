package bookstore.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Bookstore Owner Repository
 * NOTE: this class might not be necessary
 *
 * @author Thanuja Sivaananthan
 */
public interface OwnerRepository extends CrudRepository<BookOwner, Long> {

    List<BookOwner> findByUsername(String username);

    BookOwner findById(long id);
}