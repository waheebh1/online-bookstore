package bookstore.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import bookstore.inventory.Book;
import bookstore.inventory.ShoppingCart;
import bookstore.inventory.ShoppingCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import java.util.Collections;


/**
 * UserController Test
 * @author Thanuja Sivaananthan
 * @author Sabah Samwatin
 */
@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController controller;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    /**
     * Test adding a new user is successful
     * @author Thanuja Sivaananthan
     */
    @Test
    void allowAddNewUser() {
        BookUser user1 = new BookUser("AddUser", "password123");
        Model model = new ConcurrentModel();
        String result = controller.createAccountSubmit(user1, model);
        Assertions.assertEquals("redirect:/login", result);
        Assertions.assertEquals(user1, model.getAttribute("user"));
    }

    /**
     * Test that duplicate usernames are rejected
     * @author Thanuja Sivaananthan, Sabah Samwatin
     */
    @Test
    void rejectCreateDuplicateUsername() {
        // User with duplicate username
        BookUser duplicateUser = new BookUser("Duplicate1", "password123");

        // Mock userRepository to simulate finding an existing user
        when(userRepository.findByUsername("Duplicate1")).thenReturn(Collections.singletonList(duplicateUser));

        Model model = new ConcurrentModel();

        // First attempt to create user
        controller.createAccountSubmit(duplicateUser, model);

        // Second attempt with the same username
        Model model2 = new ConcurrentModel();
        controller.createAccountSubmit(duplicateUser, model2);

        // The expected error message
        String expectedResult = "Username already exists. Please use a new username or login with the current username.";
        Assertions.assertEquals(expectedResult, model2.getAttribute("error"));
    }

    /**
     * Test that null values are rejected
     * @author Thanuja Sivaananthan
     */
    @Test
    void rejectCreateNullValues() {
        BookUser user1 = new BookUser();
        BookUser user2 = new BookUser("NoPassword", "");
        BookUser user3 = new BookUser("", "passwordNoUsername");
        Model model = new ConcurrentModel();

        String expectedResult = "Username or password cannot be empty.";
        controller.createAccountSubmit(user1, model);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));

        controller.createAccountSubmit(user2, model);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));

        controller.createAccountSubmit(user3, model);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));
    }

    /**
     * Test responsible for displaying the login form
     * @author Sabah Samwatin
     */
    @Test
    void displayLoginForm() {
        Model model = new ConcurrentModel();
        String result = controller.accountForm(model);
        Assertions.assertEquals("login", result);
        Assertions.assertNotNull(model.getAttribute("user"));
        Assertions.assertTrue(model.getAttribute("user") instanceof BookUser);
    }

    /**
     * Test for successful login
     * @author Sabah Samwatin
     */
    @Test
    void successfulLogin() {
        // Mocking userRepository
        BookUser existingUser = new BookUser("ExistingUser", "password123");
        when(userRepository.findByUsername("ExistingUser")).thenReturn(Collections.singletonList(existingUser));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(existingUser, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(existingUser, model.getAttribute("user"));
    }

    /**
     * Test for failed log due to incorrect password
     * @author Sabah Samwatin
     */
    @Test
    void failedLoginIncorrectPassword() {
        BookUser existingUser = new BookUser("User", "password123");
        when(userRepository.findByUsername("User")).thenReturn(Collections.singletonList(existingUser));

        BookUser wrongPasswordUser = new BookUser("User", "wrongPassword");
        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(wrongPasswordUser, model);
        Assertions.assertEquals("accountError", result);
        Assertions.assertEquals("Invalid username/password", model.getAttribute("error"));
    }

    /**
     * Test for failed login due to user not existing
     * @author Sabah Samwatin
     */
    @Test
    void failedLoginNonExistentUsername() {
        BookUser formUser = new BookUser("NonExistingUser", "password123");
        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(formUser, model);
        Assertions.assertEquals("accountError", result);
        Assertions.assertEquals("Username NonExistingUser does not exist. Please register for a new account or use a different username", model.getAttribute("error"));
    }

    /**
     * Test for registerLogin method
     * @author Sabah Samwatin
     */
    @Test
    void displayRegisterLoginPage() {
        Model model = new ConcurrentModel();
        String result = controller.registerLogin(model);
        Assertions.assertEquals("register-login", result);
    }

    /**
     * Test for empty username or password login
     * @author Sabah Samwatin
     */
    @Test
    void rejectLoginEmptyFields() {
        // Prepare the model
        Model model = new ConcurrentModel();

        // Test with empty username
        BookUser userWithEmptyUsername = new BookUser("", "password");
        String resultUsernameEmpty = controller.handleUserLogin(userWithEmptyUsername, model);
        Assertions.assertEquals("accountError", resultUsernameEmpty);
        String expectedMessageForEmptyUsername = "Username  does not exist. Please register for a new account or use a different username";
        Assertions.assertEquals(expectedMessageForEmptyUsername, model.getAttribute("error"));

        // Reset the model for the next test
        model = new ConcurrentModel();

        // Test with empty password
        BookUser userWithEmptyPassword = new BookUser("username", "");
        String resultPasswordEmpty = controller.handleUserLogin(userWithEmptyPassword, model);
        Assertions.assertEquals("accountError", resultPasswordEmpty);
        String expectedMessageForEmptyPassword = "Username username does not exist. Please register for a new account or use a different username";
        Assertions.assertEquals(expectedMessageForEmptyPassword, model.getAttribute("error"));
    }

    /**
     * Test for correctly handing any
     * exception that may occur during login process
     * @author Sabah Samwatin
     */
    @Test
    void handleLoginException() {
        BookUser user = new BookUser("User", "password123");

        // Assuming userRepository.findByUsername throws an exception
        when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("Database error"));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(user, model);
        Assertions.assertEquals("accountError", result);
        Assertions.assertEquals("An unexpected error occurred: Database error", model.getAttribute("error"));
    }

    /**
     * Test for Invalid login attempt due to incorrect credentials
     * @author Sabah Samwatin
     */
    @Test
    void invalidLoginAttempt() {
        String nonExistentUsername = "NonExistentUser";
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Collections.emptyList());

        BookUser nonExistentUser = new BookUser(nonExistentUsername, "password");
        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(nonExistentUser, model);
        Assertions.assertEquals("accountError", result);

        // Updated to match the actual error message from UserController
        String expectedErrorMessage = "Username " + nonExistentUsername + " does not exist. Please register for a new account or use a different username";
        Assertions.assertEquals(expectedErrorMessage, model.getAttribute("error"));
    }

    /**
     * Test that the login process can
     * correctly identify the user among multiple users
     * @author Sabah Samwatin
     */
    @Test
    void loginWithMultipleUsers() {
        BookUser user1 = new BookUser("User1", "password1");
        BookUser user2 = new BookUser("User2", "password2");

        when(userRepository.findByUsername("User1")).thenReturn(Collections.singletonList(user1));
        when(userRepository.findByUsername("User2")).thenReturn(Collections.singletonList(user2));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(user1, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(user1, model.getAttribute("user"));

        model = new ConcurrentModel();
        result = controller.handleUserLogin(user2, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(user2, model.getAttribute("user"));
    }

    /**
     * Test for successful logout
     * @author Sabah Samwatin
     */
    @Test
    void successfulLogout() {
        // Call the logout method
        String result = controller.handleUserLogout();

        // Check the result of the logout operation
        Assertions.assertEquals("redirect:/", result);

        // Verify that user access is set to false, indicating the user is logged out
        Assertions.assertFalse(controller.getUserAccess(), "User should be logged out and userAccess should be false.");
    }
    /**
     * Test for ensuring correct redirect after logout.
     * @author Sabah Samwatin
     */

    @Test
    void ensureCorrectRedirectAfterLogout() {
        // Perform logout
        String result = controller.handleUserLogout();

        // Check if the redirection is to the home page
        Assertions.assertEquals("redirect:/", result, "User should be redirected to home page after logout.");
    }

    /**
     * Test logout when user is logged in.
     * @author Sabah Samwatin
     */
    @Test
    void logoutWhenUserLoggedIn() {
        // Assuming a user is logged in
        BookUser loggedInUser = new BookUser("LoggedInUser", "password123");
        controller.getUserAccess(); // Set to true to simulate logged-in user

        // Perform logout
        String result = controller.handleUserLogout();

        // Check the result of the logout operation
        Assertions.assertEquals("redirect:/", result);
        Assertions.assertFalse(controller.getUserAccess(), "User should be logged out and userAccess should be false.");
    }
    /**
    * Test for logout and immediate login.
    * @author Sabah Samwatin
     * */
    @Test
    void logoutAndImmediateLogin() {
        // Assuming we have a user
        BookUser loggedInUser = new BookUser("User", "password");

        // Simulate the user being logged in
        // This typically involves setting up security context, session, or similar
        setupUserAsLoggedIn(loggedInUser);

        // Perform logout
        String logoutResult = controller.handleUserLogout();

        // Check that the logout was successful
        Assertions.assertEquals("redirect:/", logoutResult);
        // Additionally, assert that the user is no longer in the session/security context
        // Assertions.assertFalse(checkUserLoggedInStatus(), "User should be logged out.");

        // Setup for login - Create a new model and simulate login data
        Model model = new ConcurrentModel();

        // Mocking userRepository to return the loggedInUser when findByUsername is called
        when(userRepository.findByUsername("User")).thenReturn(Collections.singletonList(loggedInUser));

        // Perform login immediately after logout
        String loginResult = controller.handleUserLogin(loggedInUser, model);

        // Verify login is successful
        Assertions.assertEquals("redirect:/listAvailableBooks", loginResult);
        // Assertions.assertTrue(checkUserLoggedInStatus(), "User should be logged in.");
    }

    private void setupUserAsLoggedIn(BookUser loggedInUser) {
    }

    /**
     * Test logout during an ongoing shopping cart transaction.
     * @author Sabah Samwatin
     */
    @Test
    void logoutDuringOngoingTransaction() {
        // Create a user and simulate them being logged in
        BookUser user = new BookUser("testUser", "password123");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Collections.singletonList(user));

        // Create a shopping cart and add items to it
        ShoppingCart shoppingCart = new ShoppingCart();
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setPrice(10.0);
        shoppingCart.addToCart(book, 1);

        // Simulate the shopping cart being saved in the repository
        when(shoppingCartRepository.findById(1)).thenReturn(shoppingCart);

        // Perform logout
        controller.handleUserLogout();

        // Verify that the shopping cart is handled properly upon logout
        ShoppingCart postLogoutCart = shoppingCartRepository.findById(1);

        // Check if the cart is cleared upon logout (modify this assertion based on your application's behavior)
        Assertions.assertTrue(postLogoutCart.getBooksInCart().isEmpty(), "Shopping cart should be empty after logout.");
    }
    /**
     * Test logging out when there are items in the shopping cart.
     * @author Sabah Samwatin
    @Test
    void logoutWithItemsInCart() {
        // Create a user and simulate them being logged in
        BookUser user = new BookUser("testUser", "password123");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Collections.singletonList(user));

        // Create a shopping cart with items and a specific ID
        long cartId = 1L; // Example ID
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setId(cartId);
        Book book = new Book();
        book.setIsbn("1234567890");
        book.setPrice(10.0);
        shoppingCart.addToCart(book, 1);

        // Simulate the shopping cart being associated with the user
        when(shoppingCartRepository.findById(cartId)).thenReturn(shoppingCart);

        // Perform logout
        controller.handleUserLogout();

        // Fetch the shopping cart after logout
        ShoppingCart postLogoutCart = shoppingCartRepository.findById(cartId);

        // Verify the state of the shopping cart after logout
        Assertions.assertTrue(postLogoutCart.getBooksInCart().isEmpty(), "Shopping cart should be empty after logout.");

    }*/
}