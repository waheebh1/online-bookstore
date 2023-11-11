/**
 * Repository for Inventory objects
 * @author Maisha Abdullah
 */
package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;
public interface InventoryItemRepository extends CrudRepository<InventoryItem, Long>{
    InventoryItem findById(long id);


}
