package bookstore.users;

/**
 * UserType enum
 *
 * @author Thanuja Sivaananthan
 */
public enum UserType {
    BOOKUSER("BOOKUSER"),
    BOOKOWNER("BOOKOWNER");

    public final String label;

    /**
     * Create new usertype
     * @author Thanuja Sivaananthan
     * @param label usertype label
     */
    UserType(String label) {
        this.label = label;
    }

}
