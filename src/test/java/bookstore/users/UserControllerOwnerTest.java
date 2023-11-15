package bookstore.users;

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
 * UserController Owner Specific Tests
 * @author Thanuja Sivaananthan
 * @author Sabah Samwatin
 */
@SpringBootTest
public class UserControllerOwnerTest {

    @Autowired
    private UserController controller;

    @MockBean
    private UserRepository userRepository;

    /**
     * Test adding a new owner is successful
     * @author Thanuja Sivaananthan
     */
    @Test
    void allowAddNewOwnerDirectly() {
        BookUser user1 = new BookOwner("AddOwnerDirectly", "password123");
        Model model = new ConcurrentModel();
        String result = controller.createAccountSubmit(user1, model);
        Assertions.assertEquals("redirect:/login", result);

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
        Assertions.assertEquals("redirect:/login", result);

        BookUser modelUser = (BookUser) model.getAttribute("user");
        Assertions.assertNotNull(modelUser);
        Assertions.assertTrue(modelUser instanceof BookOwner);
        Assertions.assertEquals(user1.getUsername(), modelUser.getUsername());
        Assertions.assertEquals(user1.getPassword(), modelUser.getPassword());
        Assertions.assertEquals(UserType.BOOKOWNER, modelUser.getUserType());
    }

    // add Owner login tests

    /**
     * Test for successful login
     * @author Sabah Samwatin
     */
    @Test
    void successfulLogin() {
        // Mocking userRepository
        BookUser existingOwner = new BookOwner("ExistingOwner", "password123");
        Mockito.when(userRepository.findByUsername("ExistingOwner")).thenReturn(Collections.singletonList(existingOwner));

        Model model = new ConcurrentModel();
        String result = controller.handleUserLogin(existingOwner, model);
        Assertions.assertEquals("redirect:/listAvailableBooks", result);
        Assertions.assertEquals(existingOwner, model.getAttribute("user"));
    }
}
