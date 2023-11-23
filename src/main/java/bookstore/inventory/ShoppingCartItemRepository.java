package bookstore.inventory;

import org.springframework.data.repository.CrudRepository;

public interface ShoppingCartItemRepository extends CrudRepository<ShoppingCartItem, Long> {
    ShoppingCartItem findById(long id);
}
