package bookstore.inventory;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Item {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "isbn", referencedColumnName = "isbn")
    private Book book;
    private int quantity;

    /**
     * Default constructor
     * @author maisha abdullah
     */
    public Item(){
    }

    /**
     * Constructor for InventoryItem
     * @param book      Book that is the item
     * @param quantity  Quantity of that item
     * @author maisha abdullah
     */
    public Item (Book book, int quantity){
        this.book = book;
        this.quantity = quantity;
    }

    /**
     * Method to set the ID
     * @param id    the ID
     * @author maisha abdullah
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Method to get the ID
     * @return  the ID
     * @author maisha abdullah
     */
    public Long getId() {
        return id;
    }

    /**
     * Method to get the Book object from the inventory item
     * @return  the Book object
     * @author maisha abdullah
     */
    public Book getBook() {
        return book;
    }

    /**
     * Method to set the Book object of the inventory item
     * @param book  the Book object
     * @author maisha abdullah
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Method to get the quantity of the inventory item
     * @return  the quantity
     * @author maisha abdullah
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Method to set the quantity of the inventory item
     * @param quantity  the quantity
     * @author maisha abdullah
     */
    public void setQuantity(int quantity) {
        if(quantity >= 0) {
            this.quantity = quantity;
        }
    }
}
