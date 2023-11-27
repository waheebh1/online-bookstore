/**
 * Repository for Inventory objects
 */
package bookstore.inventory;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryItemRepository extends CrudRepository<InventoryItem, Long> {
    InventoryItem findById(long id);

    /**
     * Sort by title alphabetically
     * @return sorted list of inventory items
     * @author Shrimei Chock
     */
    @Query("SELECT i FROM InventoryItem i ORDER BY i.book.title ASC")
    List<InventoryItem> sortByTitleAsc();

    /**
     * Sort by price low to high
     * @return sorted list of inventory items
     * @author Shrimei Chock
     */
    @Query("SELECT i FROM InventoryItem i ORDER BY i.book.price ASC")
    List<InventoryItem> sortByPriceAsc();

    /**
     * Sort by price high to low
     * @return sorted list of inventory items
     * @author Shrimei Chock
     */
    @Query("SELECT i FROM InventoryItem i ORDER BY i.book.price DESC")
    List<InventoryItem> sortByPriceDesc();
}
