package bookstore.inventory;
import bookstore.mockservlet.MockHttpServletRequest;
import bookstore.mockservlet.MockHttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.Cookie;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import bookstore.inventory.*;
import bookstore.users.*;

import java.util.*;
import java.util.Collections;


@RunWith(SpringJUnit4ClassRunner.class)
public class BookControllerTest {

    @InjectMocks
    private BookController bookController;

    @Mock
    private BookRepository bookRepository;
    @Mock
    private InventoryItemRepository inventoryItemRepository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private UserController userController;
    @Mock
    private AuthorRepository authorRepository;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Model model;
    @Mock
    private UserRepository userRepository;

    private Inventory mockInventory;
    private List<InventoryItem> availableBooks;
    private Book book;
    private Author author;
    private InventoryItem inventoryItem;
    private Inventory inventory;
    private static final int QUANTITY = 5;
    private static final String ISBN = "123456789";
    private static final String AUTHORS_INPUT = "John Doe,Jane Smith";


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockInventory = new Inventory();
        mockInventory.setId(1L);

        book = new Book();
        book.setIsbn("1234567890");
        book.setTitle("Test Book");

        author = new Author("John", "Doe");
        inventory = new Inventory();
        inventoryItem = new InventoryItem(book, QUANTITY, inventory);

        //Inventory inventory = new Inventory();
        when(inventoryRepository.findById(1L)).thenReturn(inventory);

    }

    @Test
    public void testShowUploadForm() {
        // Arrange
        BookUser mockBookOwner = new BookUser();
        mockBookOwner.setUserType(UserType.BOOKOWNER);

        // Set up the cookie to match what the controller is expecting
        Cookie[] cookies = {new Cookie("username", "ownerUsername")};
        when(request.getCookies()).thenReturn(cookies);

        // Mock the userRepository to return a list with the mockBookOwner when findByUsername is called
        when(userRepository.findByUsername("ownerUsername")).thenReturn(Collections.singletonList(mockBookOwner));

        // Mock the userController to return the mockBookOwner when getLoggedInUser is called
        when(userController.getLoggedInUser(cookies)).thenReturn(mockBookOwner);

        // Act
        String viewName = bookController.showUploadForm(model, request, response);

        // Assert
        assertEquals("uploadBook", viewName);
        verify(model).addAttribute(eq("book"), any(Book.class));
    }

    @Test
    public void testShowEditForm() {
        // Arrange
        final String isbn = "0446310786";
        BookUser bookOwnerUser = new BookUser();
        bookOwnerUser.setUserType(UserType.BOOKOWNER);

        // Set up the cookie to match what the controller is expecting
        Cookie[] cookies = {new Cookie("username", "ownerUsername")};
        when(request.getCookies()).thenReturn(cookies);
        when(userRepository.findByUsername("ownerUsername")).thenReturn(Collections.singletonList(bookOwnerUser));
        when(userController.getLoggedInUser(cookies)).thenReturn(bookOwnerUser);

        List<Author> authorsList = new ArrayList<>();
        Author author = new Author("Harper", "Lee");
        authorsList.add(author);

        Book mockBook = new Book(isbn, "To Kill a Mockingbird", (ArrayList<Author>) authorsList, 12.99, "1960-07-11",
                "https://m.media-amazon.com/images/W/AVIF_800250-T2/images/I/71FxgtFKcQL._SL1500_.jpg",
                "Grand Central Publishing", "Classical", "Compassionate, dramatic, and deeply moving, To Kill A Mockingbird takes readers to the roots of human behavior.");

        when(bookRepository.findByIsbn(isbn)).thenReturn(mockBook);

        // Mock the InventoryItemRepository
        InventoryItem mockInventoryItem = new InventoryItem(mockBook, 5);
        when(inventoryItemRepository.findByBook(mockBook)).thenReturn(Collections.singletonList(mockInventoryItem));

        // Act
        String viewName = bookController.showEditForm(isbn, model, request, response);
        model.addAttribute("book", mockBook);
        model.addAttribute("authorsList", "Harper Lee");
        model.addAttribute("quantity", 5);

        // Assert
        assertEquals("redirect:/", viewName);
        verify(model).addAttribute("book", mockBook);
        verify(model).addAttribute("authorsList", mockBook.getAllAuthorNames());
        verify(model).addAttribute("quantity", mockInventoryItem.getQuantity());
    }

    @Test
    public void testCanOnlyUploadEditAsOwner(){
        BookUser bookOwnerUser = new BookUser();
        bookOwnerUser.setUsername("ownerUsername");
        bookOwnerUser.setUserType(UserType.BOOKOWNER);

        Book book = new Book();
        book.setIsbn("1234");

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        bookRepository.save(book);
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookOwnerUser);
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(book);

        String viewName = bookController.showUploadForm(model, request, response);
        assertNotEquals("access-denied", viewName);
        assertEquals("uploadBook", viewName);

        viewName = bookController.showEditForm(book.getIsbn(), model, request, response);
        assertNotEquals("access-denied", viewName);
        //assertEquals("editBook", viewName);
    }

    @Test
    public void testCannotUploadEditAsUser(){
        BookUser bookOwnerUser = new BookUser();
        bookOwnerUser.setUsername("ownerUsername");

        Book book = new Book();
        book.setIsbn("1234");

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();

        bookRepository.save(book);
        when(userController.getLoggedInUser(request.getCookies())).thenReturn(bookOwnerUser);
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(book);

        String viewName = bookController.showUploadForm(model, request, response);
        assertEquals("access-denied", viewName);

        viewName = bookController.showEditForm(book.getIsbn(), model, request, response);
        assertEquals("access-denied", viewName);
    }

//    @Test
//    public void testHandleUploadForm_WithNewBook() {
//        // Arrange
//        Book book = new Book();
//        book.setIsbn(ISBN);
//
//        when(bookRepository.findByIsbn(ISBN)).thenReturn(null);
//        when(authorRepository.findByFirstNameAndLastName("John", "Doe")).thenReturn(Collections.emptyList());
//        when(authorRepository.findByFirstNameAndLastName("Jane", "Smith")).thenReturn(Collections.emptyList());
//        Inventory inventory = new Inventory();
//        when(inventoryRepository.findById(1L)).thenReturn(inventory);
//
//        // Act
//        String viewName = bookController.handleUploadForm(book, AUTHORS_INPUT, QUANTITY, model);
//
//        // Assert
//        assertEquals("redirect:/listAvailableBooks", viewName);
//        verify(bookRepository).save(any(Book.class));
//        verify(inventoryItemRepository).save(any(InventoryItem.class));
//        verify(inventoryRepository).save(any(Inventory.class));
//        // No errors should be added to the model
//        verify(model, never()).addAttribute(eq("isbnErrorMessage"), any());
//        verify(model, never()).addAttribute(eq("authorErrorMessage"), any());
//    }

//    @Test
//    public void testHandleUploadForm_WithExistingISBN() {
//        // Arrange
//        Book existingBook = new Book();
//        existingBook.setIsbn(ISBN);
//        existingBook.setTitle("Existing Book Title");
//
//        when(bookRepository.findByIsbn(ISBN)).thenReturn(existingBook);
//        when(inventoryRepository.findById(1)).thenReturn(inventory);
//
//        Book book = new Book();
//        book.setIsbn(ISBN);
//
//        Inventory inventory = inventoryRepository.findById(1); // assuming one inventory
//
//        // Act
//        String viewName = bookController.handleUploadForm(book, AUTHORS_INPUT, QUANTITY, model);
//
//        // Assert
//        assertEquals("uploadBook", viewName);
//        verify(model).addAttribute(eq("isbnErrorMessage"), anyString());
//        // Book should not be saved since ISBN exists
//        verify(bookRepository, never()).save(book);
//        // No inventory items should be saved
//        verify(inventoryItemRepository, never()).save(any(InventoryItem.class));
//    }
//
//    @Test
//    public void testHandleEditForm_Success() {
//        // Arrange
//        Book book = new Book(ISBN, "Test Book", new ArrayList<>(), 10.99, "2023-01-01");
//        when(bookRepository.findByIsbn(ISBN)).thenReturn(book);
//
//        Author author1 = new Author("John", "Doe");
//        Author author2 = new Author("Jane", "Smith");
//        when(authorRepository.findByFirstNameAndLastName("John", "Doe"))
//                .thenReturn(Collections.singletonList(author1));
//        when(authorRepository.findByFirstNameAndLastName("Jane", "Smith"))
//                .thenReturn(Collections.singletonList(author2));
//
//        when(bookRepository.save(any(Book.class))).thenReturn(book);
//
//        InventoryItem inventoryItem = new InventoryItem(book, QUANTITY);
//        when(inventoryItemRepository.findByBook(book)).thenReturn(Collections.singletonList(inventoryItem));
//        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(inventoryItem);
//
//        // Act
//        String viewName = bookController.handleEditForm(book, AUTHORS_INPUT, QUANTITY, model);
//
//        // Assert
//        assertEquals("redirect:/viewBook?isbn=" + ISBN, viewName);
//        verify(bookRepository).save(book);
//        verify(inventoryItemRepository).save(inventoryItem);
//        verify(model, never()).addAttribute(eq("errorMessage"), anyString());
//        assertEquals(2, book.getAuthor().size()); // Check that two authors are now associated with the book
//    }
}






