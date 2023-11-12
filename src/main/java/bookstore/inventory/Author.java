/**
 * Author object
 * @author Shrimei Chock
 */
package bookstore.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Author {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String firstName = null;
    private String lastName = null;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Book> bibliography = new ArrayList<>();

    /**
     * Default constructor
     */
    public Author(){}

    /**
     Constructor with all parameters
     * @param firstName first name
     * @param lastName last name
     */
    public Author(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Get ID
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set ID
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Get author's first name
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set author's first name
     * @param firstName first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get author's last name
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set author's last name
     * @param lastName last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get author's full name
     * @return LastName, Firstname
     */
    public String getFullName(){
        return this.lastName + ", " + this.firstName;
    }

    /**
     * Get list of author's works
     * @return bibliography
     */
    public List<Book> getBibliography() {
        return bibliography;
    }

    /**
     * Add book to bibliography
     * @param book book to add
     */
    public void addBook(Book book){
        this.bibliography.add(book);
    }

    /**
     * Remove book from bibliography
     * @param book book to remove
     */
    public void removeBook(Book book){
        this.bibliography.remove(book);
    }
}