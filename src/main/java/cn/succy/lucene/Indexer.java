package cn.succy.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

/**
 * lucene索引器
 *
 * @author Succy
 * @date 2017-03-19 10:39
 **/

public class Indexer {
    // 构造一个写的索引，用来写入索引
    private IndexWriter writer;

    public Indexer(String indexDir) throws IOException {
        // 如果索引的目录下已经存在有索引的内容，删除
        File[] files = new File(indexDir).listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        Directory dir = FSDirectory.open(new File(indexDir));
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36));
        writer = new IndexWriter(dir, conf);
    }

    /**
     * 对文档进行索引
     *
     * @param dataDir 要索引的文件的路径
     * @param filter  过滤器
     */
    public int index(String dataDir, FileFilter filter) throws IOException {
        File[] files = new File(dataDir).listFiles();
        for (File file : files) {
            if (file.exists() && file.canRead() && !file.isDirectory() && !file.isHidden()
                    && (filter == null || filter.accept(file))) {
                indexFile(file);
            }
        }

        return writer.numDocs();
    }

    public void close() throws IOException {
        if (writer != null)
            writer.close();
    }

    /**
     * 通过文件获取文档
     * 文档里边索引的是字段，一般索引的是:文件的内容、文件名、文件全路径
     *
     * @param file 要构造文档的文件
     * @return 构造好的文档
     * @throws IOException
     */
    private Document getDocument(File file) throws IOException {
        Document doc = new Document();
        // 1、索引文件的内容
        doc.add(new Field("contents", new FileReader(file)));
        // 2、索引文件的文件名
        doc.add(new Field("fileName", file.getName(), Store.YES, Index.NOT_ANALYZED));
        // 3、索引文件的全路径名
        doc.add(new Field("fullPath", file.getCanonicalPath(), Store.YES, Index.NOT_ANALYZED));
        return doc;
    }

    /**
     * 索引文件，通过索引写入器去将索引好的文档添加进去
     *
     * @param file 要索引的文件
     * @throws IOException
     */
    private void indexFile(File file) throws IOException {
        System.out.println("indexing:  " + file.getCanonicalPath());
        Document doc = getDocument(file);
        writer.addDocument(doc);
    }

    /**
     * 内部类实现FileFilter,达到过滤.txt后缀名的文件效果
     */
    static class TextFileFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".txt");
        }
    }

    public static void main(String[] args) {
        String dataDir = "H:\\lucene\\docs";
        String indexDir = "H:\\lucene\\index";

        try {
            Indexer indexer = new Indexer(indexDir);
            long start = System.currentTimeMillis();
            System.out.println("begin indexing……");
            int num = indexer.index(dataDir, new Indexer.TextFileFilter());
            long end = System.currentTimeMillis();
            System.out.println("total index " + num + " files, and elapse times: " + (end - start) + "ms");
            indexer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
