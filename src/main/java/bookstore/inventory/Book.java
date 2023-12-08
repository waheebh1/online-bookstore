package bookstore.inventory;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Book {
    @Id
    private String isbn = null;
    private String title = null;
    private String cover = null;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Author> author = new ArrayList<>();
    private String publisher = null;
    private String genre = null;
    private Double price = null;
    private String description = null;
    private String date = null;

    /**
     * Default constructor
     * @author Shrimei Chock
     */
    public Book(){}

    /**
     * Constructor with basic parameters
     * @param isbn unique ID
     * @param title book title
     * @param author book author
     * @param price cost of book
     * @param date date that book was published
     *
     * @author Shrimei Chock
     */
    public Book(String isbn, String title, ArrayList<Author> author, Double price, String date){
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.date = date;
    }

    /**
     * Constructor with all parameters
     * @param isbn unique ID
     * @param title book title
     * @param cover cover picture
     * @param author book author
     * @param publisher publishing company
     * @param genre genre
     * @param price cost of book
     * @param description short summary
     *
     * @author Shrimei Chock
     */
    public Book(String isbn, String title, ArrayList<Author> author, Double price, String date, String cover, String publisher, String genre, String description){
        this(isbn, title, author, price, date); //call basic constructor
        this.cover = cover;
        this.publisher = publisher;
        this.genre = genre;
        this.description = description;
    }

    /**
     * Get isbn
     * @return isbn
     *
     * @author Shrimei Chock
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Set isbn
     * @param isbn unique code for book
     *
     * @author Shrimei Chock
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Get title
     * @return title
     *
     * @author Shrimei Chock
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title book title
     *
     * @author Shrimei Chock
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get picture of book cover
     * @return picture
     *
     * @author Shrimei Chock
     */
    public String getCover() {
        return cover;
    }

    /**
     * Set picture
     * @param picture book cover image
     *
     * @author Shrimei Chock
     */
    public void setCover(String picture) {
        this.cover = picture;
    }

    /**
     * Get author
     * @return author
     *
     * @author Shrimei Chock
     */
    public List<Author> getAuthor() {
        return author;
    }

    /**
     * Set author
     * @param author author of book
     *
     * @author Shrimei Chock
     */
    public void setAuthor(ArrayList<Author> author) {
        this.author = author;
    }

    /**
     * Get publisher
     * @return publisher
     *
     * @author Shrimei Chock
     */
    public String getPublisher() {
        return publisher;
    }

    /**
     * Set publisher
     * @param publisher publisher of book
     *
     * @author Shrimei Chock
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    /**
     * Get date that book was released
     * @return release date
     *
     * @author Shrimei Chock
     */
    public String getDate() {
        return date;
    }

    /**
     * Set date that book was released
     * @param date release date
     *
     * @author Shrimei Chock
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Get genre of book
     * @return genre
     *
     * @author Shrimei Chock
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Set genre
     * @param genre genre of book
     *
     * @author Shrimei Chock
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Get price
     * @return price
     *
     * @author Shrimei Chock
     */
    public Double getPrice() {
        return price;
    }

    public String getFormattedPrice() {
        return String.format("%.2f", price);
    }

    /**
     * Set price
     * @param price cost of book
     *
     * @author Shrimei Chock
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Get description
     * @return description
     *
     * @author Shrimei Chock
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description short description of book
     *
     * @author Shrimei Chock
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get all author names
     * @return collected authors
     *
     * @author Sabah Samwatin
     */
    public String getAllAuthorNames() {
        return this.author.stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(Collectors.joining(", "));
    }

}
