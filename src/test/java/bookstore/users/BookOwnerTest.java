package bookstore.users;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for BookOwner
 * @author Thanuja Sivaananthan
 */
public class BookOwnerTest
{
    /**
     * Test owner creation
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testCreateBookOwner()
    {
        BookOwner bookOwner;
        bookOwner = new BookOwner();
        assertEquals(UserType.BOOKOWNER, bookOwner.getUserType());

        bookOwner = new BookOwner("User", "password123");
        assertEquals("User", bookOwner.getUsername());
        assertEquals("password123", bookOwner.getPassword());
        assertEquals(UserType.BOOKOWNER, bookOwner.getUserType());
    }
}
