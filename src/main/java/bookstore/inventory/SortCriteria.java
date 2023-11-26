package bookstore.inventory;

/**
 * @author Shrimei Chock
 */

public enum SortCriteria {
    LOW_TO_HIGH("low_to_high"),
    HIGH_TO_LOW("high_to_low"),
    ALPHABETICAL("alphabetical");

    public final String label;

    /**
     * Create new sorting criteria
     * @author Shrimei Chock
     * @param label criteria label
     */
    SortCriteria(String label) {
        this.label = label;
    }
}
