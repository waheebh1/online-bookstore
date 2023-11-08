package bookstore.users;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

// is this even needed?

public interface OwnerRepository extends CrudRepository<BookOwner, Long> {

    List<BookOwner> findByUsername(String username);

    BookOwner findById(long id);
}