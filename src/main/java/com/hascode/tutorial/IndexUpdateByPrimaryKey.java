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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class IndexUpdateByPrimaryKey {
	public static void main(final String[] args) throws CorruptIndexException,
			LockObtainFailedException, IOException {
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35,
				new StandardAnalyzer(Version.LUCENE_35));

		IndexWriter w = new IndexWriter(index, config);
		addDoc(w, "1", "test1");
		addDoc(w, "2", "test2");
		addDoc(w, "3", "test3");
		addDoc(w, "1", "test4");
		addDoc(w, "1", "test5");
		w.forceMergeDeletes();
		w.close();

		IndexReader reader = IndexReader.open(index);
		for (int i = 0; i < reader.maxDoc(); i++) {
			Document d = reader.document(i);
			System.out.println("doc from index - id: " + d.get("id")
					+ ", name: " + d.get("name"));
		}
	}

	public static void addDoc(final IndexWriter w, final String id,
			final String name) throws IOException {
		Document doc = new Document();
		doc.add(new Field("id", id, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("name", name, Field.Store.YES, Field.Index.ANALYZED));
		w.updateDocument(new Term("id", id), doc);
	}
}
