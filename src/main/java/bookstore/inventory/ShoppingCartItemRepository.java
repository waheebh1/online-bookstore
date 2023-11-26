package bookstore.inventory;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartItemRepository extends CrudRepository<ShoppingCartItem, Long> {
    ShoppingCartItem findById(long id);

    List<ShoppingCartItem> findByQuantity(int quantity);
}
