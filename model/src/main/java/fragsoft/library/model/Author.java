package fragsoft.library.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Author implements Serializable {

	private static final long serialVersionUID = 6989620699058352020L;

	private Long id;
	private String name;
	private Set<Book> books;
	
	public Author() { }

	public Author(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(cascade=CascadeType.ALL, mappedBy="author")
	public Set<Book> getBooks() {
		return books;
	}

	public void setBooks(Set<Book> books) {
		this.books = books;
	}
	
	public void addBook(Book book) {
		if(books == null) {
			books = new HashSet<Book>();
		}
		books.add(book);
	}
	
}
