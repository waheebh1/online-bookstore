package bookstore.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
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
        Assertions.assertEquals("createAccountResult", result);
        Assertions.assertEquals(user1, model.getAttribute("user"));
    }

    /**
     * Test adding a new owner is successful
     * @author Thanuja Sivaananthan
     */
    @Test
    void allowAddNewOwnerDirectly() {
        BookUser user1 = new BookOwner("AddOwnerDirectly", "password123");
        Model model = new ConcurrentModel();
        String result = controller.createAccountSubmit(user1, model);
        Assertions.assertEquals("createAccountResult", result);

        BookUser modelUser = (BookUser) model.getAttribute("user");
        Assertions.assertNotNull(modelUser);
        Assertions.assertTrue(modelUser instanceof BookOwner);
        Assertions.assertEquals(user1.getUsername(), modelUser.getUsername());
        Assertions.assertEquals(user1.getPassword(), modelUser.getPassword());
        Assertions.assertEquals(UserType.BOOKOWNER, modelUser.getUserType());
    }

    /**
     * Test adding a new owner is successful
     * @author Thanuja Sivaananthan
     */
    @Test
    void allowAddNewOwnerViaEnum() {
        BookUser user1 = new BookUser("AddOwnerViaEnum", "password123");
        Model model = new ConcurrentModel();
        user1.setUserType(UserType.BOOKOWNER);
        String result = controller.createAccountSubmit(user1, model);
        Assertions.assertEquals("createAccountResult", result);

        BookUser modelUser = (BookUser) model.getAttribute("user");
        Assertions.assertNotNull(modelUser);
        Assertions.assertTrue(modelUser instanceof BookOwner);
        Assertions.assertEquals(user1.getUsername(), modelUser.getUsername());
        Assertions.assertEquals(user1.getPassword(), modelUser.getPassword());
        Assertions.assertEquals(UserType.BOOKOWNER, modelUser.getUserType());
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
        Mockito.when(userRepository.findByUsername("Duplicate1")).thenReturn(Collections.singletonList(duplicateUser));

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
        Mockito.when(userRepository.findByUsername("ExistingUser")).thenReturn(Collections.singletonList(existingUser));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(existingUser, model);
        Assertions.assertEquals("home", result);
        Assertions.assertEquals(existingUser, model.getAttribute("user"));
    }
    /**
     * Test for failed log due to incorrect password
     * @author Sabah Samwatin
     */
    @Test
    void failedLoginIncorrectPassword() {
        BookUser existingUser = new BookUser("User", "password123");
        Mockito.when(userRepository.findByUsername("User")).thenReturn(Collections.singletonList(existingUser));

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
        Mockito.when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("Database error"));

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
        Mockito.when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Collections.emptyList());

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

        Mockito.when(userRepository.findByUsername("User1")).thenReturn(Collections.singletonList(user1));
        Mockito.when(userRepository.findByUsername("User2")).thenReturn(Collections.singletonList(user2));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(user1, model);
        Assertions.assertEquals("home", result);
        Assertions.assertEquals(user1, model.getAttribute("user"));

        model = new ConcurrentModel();
        result = controller.handleUserLogin(user2, model);
        Assertions.assertEquals("home", result);
        Assertions.assertEquals(user2, model.getAttribute("user"));
    }


}