/**
 * Repository for Shopping cart objects
 * @author Maisha Abdullah
 */
package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartRepository extends CrudRepository<ShoppingCart, String> {
    ShoppingCart findById(long id);
}
