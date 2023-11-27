/**
 * Helper functions to process list of books
 * @author Shrimei Chock
 */

package bookstore.inventory;

import java.util.List;
import java.util.stream.Collectors;

public class BookFiltering {

    public static List<Book> createBookList(List<InventoryItem> inventoryItems) {
        return inventoryItems.stream()
                .map(InventoryItem::getBook)
                .collect(Collectors.toList());
    }

    public static List<InventoryItem> getItemsInStock(List<InventoryItem> inventoryItems) {
        // Filter out items with quantity < 0
        return inventoryItems.stream()
                .filter(item -> item.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    public static List<InventoryItem> getItemsMatchingFilters(List<InventoryItem> inventoryItems, List<String> authors, List<String> genres, List<String> publishers) {
        return inventoryItems.stream()
                .filter(item -> (authors == null || item.getBook().getAuthor().stream().anyMatch(author -> authors.contains(author.getFullName()))) && (genres==null || genres.contains(item.getBook().getGenre())) && (publishers==null || publishers.contains(item.getBook().getPublisher())))
                .collect(Collectors.toList());
    }

    public static List<String> getAllAuthors(List<Book> books) {
        return books.stream()
                .flatMap(book -> book.getAuthor().stream())
                .map(Author::getFullName) // Assuming Author has a getName() method
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getAllGenres(List<Book> books) {
        return books.stream()
                .map(Book::getGenre)
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> getAllPublishers(List<Book> books) {
        return books.stream()
                .map(Book::getPublisher)
                .distinct()
                .collect(Collectors.toList());
    }
}
