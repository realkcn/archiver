package org.kbs.archiver.util;

//import org.apache.lucene.analysis.stard.StardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.kbs.archiver.lucene.Tools;
import org.kbs.archiver.*;
import org.kbs.archiver.persistence.ArticleBodyMapper;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Date;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.*;
import java.sql.*;

import javax.sql.*;

class IndexThread implements Runnable {

	private BlockingQueue<ArticleEntity> workqueue;
	private Document document;
	private Field articleidField;
	private Field bodyField;
	private IndexWriter writer;

	public IndexThread(IndexWriter writer,BlockingQueue<ArticleEntity> queue) {
		this.workqueue = queue;
		this.writer=writer;
		document=new Document();
		articleidField=new Field("articleid","",Field.Store.YES, Field.Index.NO);
		bodyField=new Field("body","",Field.Store.NO, Field.Index.ANALYZED);
	}

	public void add(ArticleEntity article) {
		try {
			workqueue.put(article);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				ArticleEntity article = workqueue.take();
				if (article.getArticleid()==-1)
					break;
				articleidField.setValue(new Long(article.getArticleid()).toString());
				document.add(articleidField);
				document.add(bodyField);
				writer.addDocument(document);
				document.removeField("articleid");
				document.removeField("body");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
public class IndexArticle {

	private static ClassPathXmlApplicationContext appContext;

	public static void init() {
		appContext = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		appContext.registerShutdownHook();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 先index标题吧
		init();
		DataSource ds = (DataSource) appContext.getBean("dataSource");
		try {
			Properties config = (Properties) appContext
					.getBean("configproperties");
			ArticleBodyMapper articleBodyMapper = (ArticleBodyMapper) appContext
					.getBean("articleBodyMapper");
			File index = new File(Tools.getLucenceDirectory(appContext)+"new");
			Analyzer analyzer = new IKAnalyzer();// 采用的分词器
			LimitTokenCountAnalyzer limitanalyzer = new LimitTokenCountAnalyzer(
					analyzer, 1000);
			IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_35,
					limitanalyzer);
			// conf.setMaxBufferedDocs(10240);
			conf.setOpenMode(OpenMode.CREATE);
			int nThreads = 0;
			if (config.get("workerthreads") != null)
				nThreads = Integer.parseInt((String) config
						.get("workerthreads"));
			if (nThreads <= 0)
				nThreads = 4;
			//conf.setMaxThreadStates(nThreads);
			// conf.setMaxBufferedDocs(500);

			IndexWriter writer = new IndexWriter(FSDirectory.open(index), conf);
			// writer.setMaxFieldLength(200);
			long startTime = new Date().getTime();

			Connection connection = ds.getConnection();
			Statement statement = connection.createStatement();
			ResultSet rs = statement
					.executeQuery("select article.articleid,article.subject from article,board where board.boardid=article.boardid and board.ishidden=false and article.isvisible=true");
			int count = 0;
			Thread [] workerthread=new Thread[nThreads];
			ArrayBlockingQueue<ArticleEntity> workqueue=new ArrayBlockingQueue<ArticleEntity>(1000);
			for (int i=0;i<nThreads;i++) {
				workerthread[i]=new Thread(new IndexThread(writer,workqueue));
			}
			while (rs.next()) {
				Document doc = new Document();
				count++;
				ArticleEntity article;
				article=new ArticleEntity();
				article.setArticleid(rs.getLong("articleid"));
				String body = articleBodyMapper.get(rs.getLong("articleid"));
				if (body != null)
					article.setBody(body);
				else {
					System.err.println("Found article:"+article.getArticleid()+" without body");
					article.setBody("");
				}
				workqueue.put(article);
				if ((count % 200000) == 0)
					System.out.println("索引文章 " + count + " 篇");
			}
			//设置线程结束标志
			ArticleEntity article=new ArticleEntity();
			article.setArticleid(-1);
			for (int i=0;i<nThreads;i++) {
				workqueue.put(article);
			}
			for (int i=0;i<nThreads;i++) {
				workerthread[i].join();
			}
			// writer.optimize();
			long endTime = new Date().getTime();
			System.out.println("文章数：" + count);
			System.out.println("共建索引数：" + writer.numDocs());
			System.out.println("时间：" + (endTime - startTime) + " 毫秒");
			writer.close();
			rs.close();
			connection.close();
		} catch (SQLException e) {
			System.err.println("数据库操作失败：");
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
