package com.dao;

import com.entity.User;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final String path = "D:\\lucene_index";

    public static void main(String[] args) {
        UserDao userDao = new UserDao();
//        userDao.createIndex();
        userDao.searchDocByIndex();
    }

    public void searchDocByIndex() {
        try {
            Analyzer analyzer = new StandardAnalyzer();
            QueryParser queryParser = new QueryParser("name", analyzer);
            Query query = queryParser.parse("name:admin");
            Directory directory = FSDirectory.open(Paths.get(path));
            IndexReader reader = DirectoryReader.open(directory);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, 10);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            System.out.println("查询出文档个数为：" + topDocs.totalHits);
            for (ScoreDoc scoreDoc : scoreDocs) {
                int docId = scoreDoc.doc;
                Document doc = searcher.doc(docId);
                System.out.println("id:" + docId);
                System.out.println("name:" + doc.get("name"));
                System.out.println("age:" + doc.get("age"));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createIndex() {
        UserDao userDao = new UserDao();

        try {
            Analyzer analyzer = new StandardAnalyzer();
            Directory directory = FSDirectory.open(Paths.get(path));
            IndexWriterConfig cfg = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, cfg);
            writer.addDocuments(userDao.getDocuments(userDao.getAll()));
            writer.close();
            System.out.println("创建索引库成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //将数据转换成lucene文档
    public List<Document> getDocuments(List<User> list) {
        List<Document> docList = new ArrayList<>();

        Document doc = null;
        for (User user : list) {
            doc = new Document();

            Field id = new TextField("id", user.getId().toString(), Field.Store.YES);
            Field name = new TextField("name", user.getName().toString(), Field.Store.YES);
            Field age = new TextField("age", user.getAge().toString(), Field.Store.YES);

            doc.add(id);
            doc.add(name);
            doc.add(age);
            docList.add(doc);
        }
        return docList;
    }

    public List<User> getAll() {
        List<User> list = new ArrayList<>();
        list.add(new User(1, "admin", 20));
        list.add(new User(2, "admin2", 21));
        list.add(new User(3, "admin3", 22));
        return list;
    }

}
