package fragsoft.library.search;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fragsoft.library.model.Author;
import fragsoft.library.model.Book;

public final class LibrarySearchOperationsImpl implements LibrarySearchOperations {
	
	private static final Logger log = LoggerFactory.getLogger(LibrarySearchOperationsImpl.class);
	private static final String INDEX_LOCATION = "/home/yannis/workspaces/eclipse/rest/library/index";
	private Directory ramDir = null;
	
	private static final LibrarySearchOperations instance = new LibrarySearchOperationsImpl();
	
	private LibrarySearchOperationsImpl() { 
		 try {
			ramDir = FSDirectory.open(new File(INDEX_LOCATION));
		} catch (IOException ioe) {
			log.error("Could not initialize index location! ", ioe);
		}
	}
	
	public static LibrarySearchOperations getInstance() {
		return instance;
	}
	
	private IndexWriter getWriter() {
		log.debug("Creating writer.");
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(ramDir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return writer;
	}
	
	private IndexSearcher getSearcher() {
		IndexSearcher searcher = null;
		try {
			searcher = new IndexSearcher(ramDir, true);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return searcher;
	}
	
	public void index(Author author) {
		Document doc = new Document();
		Field authorName = new Field("author.name", author.getName(), Store.YES, Index.ANALYZED);
		doc.add(authorName);
		Field bookField = null;
		for(Book book: author.getBooks()) {
			bookField = new Field("book.name", book.getTitle(), Store.YES, Index.ANALYZED);
			doc.add(bookField);
		}
		IndexWriter writer = getWriter();
		try {
			writer.addDocument(doc);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (CorruptIndexException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Set<Author> search(String queryString) {
		Set<Author> authors = new HashSet<Author>();
		IndexSearcher searcher = getSearcher(); 
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
		QueryParser parser = new QueryParser(Version.LUCENE_29, "author.name", analyzer);
		Query query = null;
		TopDocs hits = null;
		
		try {
			query = parser.parse(queryString);
			hits = searcher.search(query, 10);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		try {
			for(ScoreDoc scoreDoc: hits.scoreDocs) {
				Document doc = searcher.doc(scoreDoc.doc);
				Author author = new Author(doc.getField("author.name").stringValue());
				authors.add(author);
			}
		} catch(Exception e) {
			log.error("Error while creating results from query");
		} finally {
			if(searcher != null) {
				try {
					searcher.close();
				} catch (IOException ioe) {
					log.error("Error while trying to close the index searcher. ", ioe);
				}
			}
		}

		return authors;
	}

}

