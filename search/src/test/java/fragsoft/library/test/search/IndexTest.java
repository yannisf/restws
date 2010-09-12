package fragsoft.library.test.search;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import fragsoft.library.model.Author;
import fragsoft.library.model.Book;
import fragsoft.library.search.LibrarySearchOperationsImpl;

public class IndexTest {
	
	private static final Logger log = LoggerFactory.getLogger(IndexTest.class);
	
	@Test
	public void insertAuthor() {
		log.info("Indexing author");
		Author author = new Author("J. D. Salinger");
		Book book = new Book("The Catcher in the Rye");
		author.addBook(book);
		LibrarySearchOperationsImpl.getInstance().index(author);
	}

	@Test(dependsOnMethods={"insertAuthor"})
	public void findAuthors() {
		log.info("Finding author");
		Set<Author> authors = LibrarySearchOperationsImpl.getInstance().search("Salinger");
		log.debug("Authors found: {}", authors);
	}
	
}
