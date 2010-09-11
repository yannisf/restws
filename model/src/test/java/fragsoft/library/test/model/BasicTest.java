package fragsoft.library.test.model;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import fragsoft.library.model.Author;
import fragsoft.library.model.Book;

public class BasicTest {
	
	private Logger log = LoggerFactory.getLogger(BasicTest.class);
	private EntityManagerFactory emf = Persistence.createEntityManagerFactory("library");
	
	private EntityManager getEm() {
		return emf.createEntityManager();
	}
	
	@Test
	public void insertAuthorWithBookTest() {
		log.info("Inserting Author");
		EntityManager em = getEm();
		EntityTransaction trx = em.getTransaction();
		trx.begin();
		Author author = new Author("Anonymous");
		Book book1 = new Book("Book1", author);
		Book book2 = new Book("Book2", author);
		author.addBook(book1);
		author.addBook(book2);
		em.persist(author);
		trx.commit();
		em.close();
	}

	@Test
	public void retrieveAuthorTest() {
		log.info("Finding Author");
		EntityManager em = getEm();
		EntityTransaction trx = em.getTransaction();
		trx.begin();
		Author author = em.find(Author.class, 1L);
		Assert.assertNotNull(author);
		Assert.assertTrue(author.getBooks().size() == 2);
		trx.commit();
		em.close();
	}
}
