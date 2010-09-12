package fragsoft.library.search;

import java.util.Set;

import fragsoft.library.model.Author;

public interface LibrarySearchOperations {
	
	public void index(Author author);
	
	public Set<Author> search(String query);

}
