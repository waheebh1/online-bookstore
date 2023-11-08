/**
 * Book object
 * @author Shrimei Chock
 */

package bookstore.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;

@Entity
public class Book {
    @Id
    private Integer isbn = null;
    private String title = null;
    private String picture = null;
    @ManyToMany(fetch = FetchType.EAGER)
    private ArrayList<Author> author = new ArrayList<>();
    private String publisher = null;
    private String genre = null;
    private Float price = null;
    private String description = null;

    /**
     * Default constructor
     */
    public Book(){}

    /**
     * Constructor with basic parameters
     * @param isbn unique ID
     * @param title book title
     * @param author book author
     * @param price cost of book
     */
    public Book(Integer isbn, String title, ArrayList<Author> author, Float price){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
    }

    /**
     * Constructor with all parameters
     * @param isbn unique ID
     * @param title book title
     * @param picture cover picture
     * @param author book author
     * @param publisher publishing company
     * @param genre genre
     * @param price cost of book
     * @param description short summary
     */
    public Book(Integer isbn, String title, String picture, ArrayList<Author> author, String publisher, String genre, Float price, String description){
        this(isbn, title, author, price); //call basic constructor
        this.picture = picture;
        this.publisher = publisher;
        this.genre = genre;
        this.description = description;
    }

    /**
     * Get isbn
     * @return isbn
     */
    public Integer getIsbn() {
        return isbn;
    }

    /**
     * Set isbn
     * @param isbn unique code for book
     */
    public void setIsbn(Integer isbn) {
        this.isbn = isbn;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title book title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get picture of book cover
     * @return picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Set picture
     * @param picture book cover image
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Get author
     * @return author
     */
    public ArrayList<Author> getAuthor() {
        return author;
    }

    /**
     * Set author
     * @param author author of book
     */
    public void setAuthor(ArrayList<Author> author) {
        this.author = author;
    }

    /**
     * Get publisher
     * @return publisher
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set publisher
     * @param publisher publisher of book
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Get genre of book
     * @return genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Set genre
     * @param genre genre of book
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Get price
     * @return price
     */
    public Float getPrice() {
        return price;
    }

    /**
     * Set price
     * @param price cost of book
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
     * Get description
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description short description of book
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
