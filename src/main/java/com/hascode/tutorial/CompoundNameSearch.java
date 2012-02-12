package com.hascode.tutorial;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class CompoundNameSearch {

	public static void main(final String[] args) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35,
				new StandardAnalyzer(Version.LUCENE_35));

		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "Jean-Claude", "Van Damme");
		addDoc(w, "Marc", "Peters");
		addDoc(w, "Jean", "Michelle-Gerbaud");
		addDoc(w, "Sean", "Peterson");
		addDoc(w, "Michelle", "Sanders-Michaels");
		addDoc(w, "Luc", "Gerbaud");
		w.close();

		searchForName(index, "Jean-Claude");
		searchForName(index, "Peters");
		searchForName(index, "Michelle-Gerbaud");
	}

	private static void searchForName(final Directory index, final String term)
			throws CorruptIndexException, IOException {
		System.out.println("searching for the term: " + term);
		IndexReader reader = IndexReader.open(index);
		IndexSearcher searcher = new IndexSearcher(reader);
		WildcardQuery query = new WildcardQuery(new Term("name", "*" + term
				+ "*"));
		TopDocs result = searcher.search(query, 20);
		for (ScoreDoc scoreDoc : result.scoreDocs) {
			Document d = searcher.doc(scoreDoc.doc);
			System.out.println("found entry with name: " + d.get("name"));
		}
	}

	public static void addDoc(final IndexWriter w, final String firstName,
			final String lastName) throws IOException {
		Document doc = new Document();
		doc.add(new Field("firstname", firstName, Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("lastname", lastName, Field.Store.YES,
				Field.Index.ANALYZED));
		doc.add(new Field("name", firstName + " " + lastName, Field.Store.YES,
				Field.Index.NOT_ANALYZED));
		w.addDocument(doc);
	}
}
