package bookstore.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Usersession Repository
 *
 * @author Thanuja Sivaananthan
 */
public interface UsersessionRepository extends CrudRepository<Usersession, Long> {

    Usersession findByBookUser(BookUser bookUser);

    Usersession findById(long id);
}