package bookstore.users;

/**
 * UserType enum
 *
 * @author Thanuja Sivaananthan
 */
public enum UserType {
    BOOKUSER("U"),
    BOOKOWNER("O");

    public final String label;

    /**
     * Create new usertype
     * @author Thanuja Sivaananthan
     * @param label usertype label
     */
    private UserType(String label) {
        this.label = label;
    }

}
