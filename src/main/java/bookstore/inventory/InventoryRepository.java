/**
 * Repository for Inventory objects
 * @author Maisha Abdullah
 */
package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;
public interface InventoryRepository extends CrudRepository<Inventory, Long> {
    Inventory findById(long id);
}
