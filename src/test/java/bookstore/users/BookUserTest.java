package bookstore.users;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for BookUser
 * @author Thanuja Sivaananthan
 */
public class BookUserTest
{
    /**
     * Test user creation
     * @author Thanuja Sivaananthan
     */
    @Test
    public void testCreateBookUser()
    {
        BookUser bookUser;
        bookUser = new BookUser();
        assertEquals(UserType.BOOKUSER, bookUser.getUserType());

        bookUser = new BookUser("User", "password123");
        assertEquals("User", bookUser.getUsername());
        assertEquals("password123", bookUser.getPassword());
        assertEquals(UserType.BOOKUSER, bookUser.getUserType());
    }
}
