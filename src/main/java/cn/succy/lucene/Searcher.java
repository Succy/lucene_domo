package cn.succy.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;

/**
 * 搜索器
 *
 * @author Succy
 * @date 2017-03-19 11:54
 **/

public class Searcher {
    public void search(String indexDir, String keyword) throws Exception {
        // 构造索引搜索器
        IndexReader reader = IndexReader.open(FSDirectory.open(new File(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);

        // 构造查询解析器
        QueryParser parser = new QueryParser(Version.LUCENE_36, "contents", new StandardAnalyzer(Version.LUCENE_36));
        Query query = parser.parse(keyword);

        // 进行搜索
        long start = System.currentTimeMillis();
        TopDocs docs = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        System.out.println(String.format("共匹配到%d个文档匹配关键字%s，总共耗时%d ms", docs.totalHits, keyword, (end - start)));

        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println(doc.get("fullPath"));
        }
        // 关闭资源
        searcher.close();
    }

    public static void main(String[] args) {
        String indexDir = "H:\\lucene\\index";
        String keyword = "Contributor";
        Searcher searcher = new Searcher();
        try {
            searcher.search(indexDir, keyword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
