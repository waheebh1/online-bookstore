package bookstore.users;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;


/**
 * UserController Test
 * @author Thanuja Sivaananthan
 */
@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController controller;

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
     * @author Thanuja Sivaananthan
     */
    @Test
    void rejectCreateDuplicateUsername() {
        BookUser user1 = new BookUser("Duplicate1", "password123");
        BookUser user2 = new BookUser("Duplicate1", "password123");
        Model model = new ConcurrentModel();

        String expectedResult = "Username already exists. Please use a new username or login with the current username.";
        String result;
        result = controller.createAccountSubmit(user1, model);
        Assertions.assertEquals("createAccountResult", result);
        Assertions.assertEquals(user1, model.getAttribute("user"));
        result = controller.createAccountSubmit(user2, model);
        Assertions.assertEquals("createAccountAccountError", result);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));
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
        String result;
        result = controller.createAccountSubmit(user1, model);
        Assertions.assertEquals("createAccountAccountError", result);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));

        result = controller.createAccountSubmit(user2, model);
        Assertions.assertEquals("createAccountAccountError", result);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));

        result = controller.createAccountSubmit(user3, model);
        Assertions.assertEquals("createAccountAccountError", result);
        Assertions.assertEquals(expectedResult, model.getAttribute("error"));
    }


}