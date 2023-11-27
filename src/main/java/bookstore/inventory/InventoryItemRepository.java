/**
 * Repository for Inventory objects
 * @author Maisha Abdullah
 * @author Shrimei Chock
 */
package bookstore.inventory;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryItemRepository extends CrudRepository<InventoryItem, Long> {
    InventoryItem findById(long id);

    //Sort by title alphabetically
    @Query("SELECT i FROM InventoryItem i ORDER BY i.book.title ASC")
    List<InventoryItem> sortByTitleAsc();

    //Sort by price low_to_high
    @Query("SELECT i FROM InventoryItem i ORDER BY i.book.price ASC")
    List<InventoryItem> sortByPriceAsc();

    //Sort by price high_to_low
    @Query("SELECT i FROM InventoryItem i ORDER BY i.book.price DESC")
    List<InventoryItem> sortByPriceDesc();
}
