package bookstore.inventory;

/**
 * Criteria for filtering books
 * @author Shrimei Chock
 */
public enum FilterCriteria {
    GENRE("genre"),
    AUTHOR("author"),
    PUBLISHER("publisher"),
    PRICE("price");

    public final String label;

    /**
     * Create new filtering criteria
     * @author Shrimei Chock
     * @param label criteria label
     */
    FilterCriteria(String label) {
        this.label = label;
    }
}
