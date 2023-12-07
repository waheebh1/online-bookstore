/**
 * Helper functions to process list of books
 */

package bookstore.inventory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookFiltering {

    /**
     * Convert list of inventory items into list of book objects
     * @param inventoryItems list of inventory items
     * @return list of books
     * @author Shrimei Chock
     */
    public static List<Book> createBookList(List<InventoryItem> inventoryItems) {
        return inventoryItems.stream()
                .map(InventoryItem::getBook)
                .collect(Collectors.toList());
    }

    /**
     * Return a list of inventory items that are in stock
     * @param inventoryItems list of inventory items
     * @return list of inventory items with quantity > 0
     * @author Shrimei Chock
     */
    public static List<InventoryItem> getItemsInStock(List<InventoryItem> inventoryItems) {
        // Filter out items with quantity < 0
        return inventoryItems.stream()
                .filter(item -> item.getQuantity() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Return a list of inventory items that match the given filters
     * @param inventoryItems list of inventory items
     * @param authors list of authors to filter by
     * @param genres list of genres to filter by
     * @param publishers list of publishers to filter by
     * @param price_range max price to filter by
     * @return list of inventory items that match all the selected filters
     * @author Shrimei Chock
     */
    public static List<InventoryItem> getItemsMatchingFilters(List<InventoryItem> inventoryItems, List<String> authors, List<String> genres, List<String> publishers, Double price_range) {
        return inventoryItems.stream()
                .filter(item -> (
                        authors == null || item.getBook().getAuthor().stream().anyMatch(author -> authors.contains(author.getFullName())))
                        && (genres==null || genres.contains(item.getBook().getGenre()))
                        && (publishers==null || publishers.contains(item.getBook().getPublisher()))
                        && (item.getBook().getPrice() <= price_range)
                )
                .collect(Collectors.toList());
    }

    /**
     * Return list of all authors present in inventory (does not include items that are out of stock)
     * @param books list of books in inventory
     * @return list of authors for books in inventory
     * @author Shrimei Chock
     */
    public static List<String> getAllAuthors(List<Book> books) {
        return books.stream()
                .flatMap(book -> book.getAuthor().stream())
                .map(Author::getFullName) // Assuming Author has a getName() method
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Return list of all genres present in inventory
     * @param books list of books in inventory
     * @return list of genres for books in inventory
     * @author Shrimei Chock
     */
    public static List<String> getAllGenres(List<Book> books) {
        return books.stream()
                .map(Book::getGenre)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Return list of all publishers present in inventory
     * @param books list of books in inventory
     * @return list of publishers for books in inventory
     * @author Shrimei Chock
     */
    public static List<String> getAllPublishers(List<Book> books) {
        return books.stream()
                .map(Book::getPublisher)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Returns the book with the lowest price from the given list of books.
     *
     * @param books list of books in inventory
     * @return book with the lowest price
     * @author Shrimei Chock
     */
    public static Book getBookWithLowestPrice(List<Book> books) {
        return books.stream()
                .min(Comparator.comparing(Book::getPrice))
                .orElse(null); // Returns null if the list is empty
    }

    /**
     * Returns the book with the highest price from the given list of books.
     *
     * @param books list of books in inventory
     * @return book with the highest price
     * @author Shrimei Chock
     */
    public static Book getBookWithHighestPrice(List<Book> books) {
        return books.stream()
                .max(Comparator.comparing(Book::getPrice))
                .orElse(null); // Returns null if the list is empty
    }
}
