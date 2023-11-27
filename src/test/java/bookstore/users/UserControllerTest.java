package bookstore.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import bookstore.inventory.Book;
import bookstore.inventory.ShoppingCart;
import bookstore.inventory.ShoppingCartItem;
import bookstore.inventory.ShoppingCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import java.util.Collections;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

import bookstore.users.UserController;
import bookstore.users.BookUser;
import bookstore.users.Usersession;
import bookstore.users.UsersessionRepository;
import bookstore.inventory.Inventory;
import org.springframework.ui.Model;
import org.springframework.ui.ConcurrentModel;

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
  
    @MockBean
    private UsersessionRepository usersessionRepository;

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
        Assertions.assertNotNull(((BookUser) Objects.requireNonNull(model.getAttribute("user"))).getShoppingCart());
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
        existingUser.setShoppingCart(new ShoppingCart());
        when(userRepository.findByUsername("ExistingUser")).thenReturn(Collections.singletonList(existingUser));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(existingUser, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(existingUser, model.getAttribute("user"));
        Assertions.assertNotNull(((BookUser) Objects.requireNonNull(model.getAttribute("user"))).getShoppingCart());
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
        user1.setShoppingCart(new ShoppingCart());
        BookUser user2 = new BookUser("User2", "password2");
        user2.setShoppingCart(new ShoppingCart());

        when(userRepository.findByUsername("User1")).thenReturn(Collections.singletonList(user1));
        when(userRepository.findByUsername("User2")).thenReturn(Collections.singletonList(user2));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(user1, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(user1, model.getAttribute("user"));
        Assertions.assertNotNull(((BookUser) Objects.requireNonNull(model.getAttribute("user"))).getShoppingCart());

        model = new ConcurrentModel();
        result = controller.handleUserLogin(user2, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(user2, model.getAttribute("user"));
        Assertions.assertNotNull(((BookUser) Objects.requireNonNull(model.getAttribute("user"))).getShoppingCart());
    }

    /**
     * Test for successful logout
     * @author Sabah Samwatin
     */
    @Test
    void successfulLogout() {
        // Prepare a mock user and session
        BookUser mockUser = new BookUser("TestUser", "password123");
        Usersession mockSession = new Usersession(mockUser);

        // Stub the usersessionRepository.findByBookUser() to return the mock session
        when(usersessionRepository.findByBookUser(mockUser)).thenReturn(mockSession);

        // Stub the usersessionRepository.findAll() to return a list with the mock session
        when(usersessionRepository.findAll()).thenReturn(Collections.singletonList(mockSession));

        // Perform the logout
        String result = controller.handleUserLogout();

        // Assert the user is logged out
        Assertions.assertEquals("redirect:/", result);
        Assertions.assertFalse(controller.getUserAccess());

        // Verify that the delete method was called on the usersessionRepository with the correct Usersession
        verify(usersessionRepository).delete(mockSession);
    }

    /**
     * Test for ensuring correct redirect after logout.
     * @author Sabah Samwatin
     */

    @Test
    void ensureCorrectRedirectAfterLogout() {
        // Prepare a mock user and session
        BookUser mockUser = new BookUser("TestUser", "password123");
        Usersession mockSession = new Usersession(mockUser);

        // Stub the usersessionRepository.findByBookUser() to return the mock session
        when(usersessionRepository.findByBookUser(mockUser)).thenReturn(mockSession);

        // Stub the usersessionRepository.findAll() to return a list with the mock session
        when(usersessionRepository.findAll()).thenReturn(Collections.singletonList(mockSession));

        // Perform the logout
        String viewName = controller.handleUserLogout();

        // Assert the correct view name is returned
        Assertions.assertEquals("redirect:/", viewName, "The redirect after logout should go to the home page.");

        // Additional verifications can be performed here if necessary
        // For example, verify that the session is invalidated, userAccess is set to false, etc.
    }

    /**
     * Test logout when user is logged in.
     * @author Sabah Samwatin
     */
    @Test
    void testLogoutWhenUserIsLoggedIn() {
        // Given a logged-in user
        BookUser loggedInUser = new BookUser("LoggedInUser", "securepassword");
        Usersession usersession = new Usersession(loggedInUser);

        // Mock the usersessionRepository to return a session indicating the user is logged in
        when(usersessionRepository.findByBookUser(loggedInUser)).thenReturn(usersession);

        // Mock the usersessionRepository.findAll() to return a list containing the usersession
        when(usersessionRepository.findAll()).thenReturn(Collections.singletonList(usersession));

        // When logout is attempted
        String viewName = controller.handleUserLogout();

        // Then the user should be logged out
        Assertions.assertFalse(controller.getUserAccess(), "User access should be set to false after logout.");

        // Then the usersession should be deleted
        verify(usersessionRepository).delete(usersession);

        // And the user should be redirected to the home page
        Assertions.assertEquals("redirect:/", viewName, "The user should be redirected to the home page after logout.");
    }

    /**
    * Test for logout and immediate login.
    * @author Sabah Samwatin
     * */
    @Test
    void testLogoutAndImmediateLogin() {
        // Prepare a mock user and session
        BookUser user = new BookUser("User", "password");
        Usersession session = new Usersession(user);

        // Mock the usersessionRepository to simulate the user being logged in
        when(usersessionRepository.findByBookUser(user)).thenReturn(session);
        when(usersessionRepository.findAll()).thenReturn(Collections.singletonList(session));

        // Perform the logout
        controller.handleUserLogout();

        // Assert that the user has been logged out
        Assertions.assertFalse(controller.getUserAccess());

        // Setup for immediate login after logout
        when(usersessionRepository.findByBookUser(user)).thenReturn(null); // No session should be found after logout
        when(userRepository.findByUsername("User")).thenReturn(Collections.singletonList(user));
        when(usersessionRepository.save(any(Usersession.class))).thenReturn(new Usersession(user));

        // Simulate login
        Model loginModel = new ConcurrentModel();
        String loginView = controller.handleUserLogin(user, loginModel);

        // Verify login success
        Assertions.assertEquals("redirect:/listAvailableBooks", loginView);
        Assertions.assertTrue(controller.getUserAccess());
        verify(usersessionRepository).save(any(Usersession.class)); // Verify that a new session is created
    }


    private void setupUserAsLoggedIn(BookUser loggedInUser) {
    }

    /**
     * Test logout during an ongoing shopping cart transaction.
     * @author Sabah Samwatin
     */
    @Test
    void testLogoutDuringOngoingShoppingCartTransaction() {
        // Given a user with an ongoing shopping cart transaction
        BookUser user = new BookUser("User", "password");
        Usersession session = new Usersession(user);
        Book book = new Book("1234567890", "Book Title", new ArrayList<>(), 20.00, "2020-01-01");
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addToCart(book, 1); // Add an item to the cart

        // Mock the repository interactions
        when(usersessionRepository.findByBookUser(user)).thenReturn(session);
        when(usersessionRepository.findAll()).thenReturn(Collections.singletonList(session));
        // Assume we have a ShoppingCartRepository or a method to find the shopping cart by user
        // when(shoppingCartRepository.findByUser(user)).thenReturn(shoppingCart);

        // When logout is performed
        controller.handleUserLogout();

        // Then verify that the user's session is ended
        Assertions.assertFalse(controller.getUserAccess(), "User should not have access after logout.");
        verify(usersessionRepository).delete(session);

        // Verify any additional logic that should occur during logout
        // If there is logic to handle the shopping cart during logout, it should be verified here
        // e.g., verify(shoppingCartRepository).save(shoppingCart);

        // And verify the user is redirected to the home page or login page
        String expectedView = "redirect:/"; // If this is the correct behavior
        Assertions.assertEquals(expectedView, controller.handleUserLogout(), "The user should be redirected after logout.");

    }
    /** @Test
    void testLogoutWithItemsInShoppingCart() {
        // Arrange
        BookUser user = new BookUser("User", "password123");
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(1L);
        Book book = new Book("1234567890", "Book Title", new ArrayList<>(), 20.00, "2020-01-01");
        shoppingCart.addToCart(book, 1); // Add an item to the cart
        Usersession session = new Usersession(user);

        when(usersessionRepository.findByBookUser(user)).thenReturn(session);
        when(usersessionRepository.findAll()).thenReturn(Collections.singletonList(session));
        when(shoppingCartRepository.findById(1L)).thenReturn(shoppingCart); // Direct return without Optional

        // Act
        String viewName = controller.handleUserLogout(); // Perform logout

        // Assert
        Assertions.assertFalse(controller.getUserAccess(), "User should not have access after logout.");
        verify(usersessionRepository).delete(session);
        Assertions.assertEquals("redirect:/", viewName, "The user should be redirected to the home page after logout.");

        // Simulate user logging back in
        when(userRepository.findByUsername("User")).thenReturn(Collections.singletonList(user));
        when(usersessionRepository.save(any(Usersession.class))).thenReturn(session);
        when(shoppingCartRepository.findById(1L)).thenReturn(shoppingCart); // Ensure the cart can be retrieved

        Model model = new ConcurrentModel();
        controller.handleUserLogin(user, model); // Perform login

        // Retrieve the cart after login
        ShoppingCart retrievedCart = shoppingCartRepository.findById(1L);
        Assertions.assertNotNull(retrievedCart, "Shopping cart should be retrieved after login.");

        // Check if the expected book is in the cart
        boolean bookInCart = false;
        for (ShoppingCartItem cartItem : retrievedCart.getBooksInCart()) {
            if (cartItem.getBook().getIsbn().equals(book.getIsbn())) {
                bookInCart = true;
                break;
            }
        }

        Assertions.assertTrue(bookInCart, "Shopping cart should contain the previously added book.");
    }*/


}