package bookstore.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<BookUser, Long> {

    List<BookUser> findByUsername(String username);

    BookUser findById(long id);
}