package bookstore.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Bookstore User Repository
 *
 * @author Thanuja Sivaananthan
 */
public interface UserRepository extends CrudRepository<BookUser, Long> {

    List<BookUser> findByUsername(String username);

    BookUser findById(long id);
}