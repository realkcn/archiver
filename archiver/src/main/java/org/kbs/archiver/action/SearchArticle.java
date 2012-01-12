package org.kbs.archiver.action;

import java.io.File;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.struts2.ServletActionContext;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.lucene.Tools;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.util.Pager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wltea.analyzer.lucene.*;

import com.opensymphony.xwork2.ActionSupport;

public class SearchArticle extends ActionSupport {
	private String subject = null;
	private ArticleMapper articleMapper = null;
	private IndexReader indexReader = null;
	private Pager pager;
	private int inputPageno;
	private ArrayList<ArticleEntity> articlelist = null;

	public String Search() throws Exception {
		int maxSerach = 10000;
		if (indexReader == null) {
			synchronized (SearchArticle.class) {
				if (indexReader == null) {
					WebApplicationContext webApplicationContext = WebApplicationContextUtils
							.getRequiredWebApplicationContext(ServletActionContext
									.getServletContext());
					indexReader = IndexReader.open(FSDirectory.open(new File(
							Tools.getLucenceDirectory(webApplicationContext))));
				}
			}
		}
		IndexSearcher searcher = new IndexSearcher(indexReader);
		searcher.setSimilarity(new IKSimilarity());
		// Query query = IKQueryParser.parse("subject", subject);
		Analyzer analyzer = new IKAnalyzer();
		QueryParser queryParser = new QueryParser(Version.LUCENE_35, "subject",
				analyzer);
		Query query = queryParser.parse(subject);
		TopDocs hits = searcher.search(query, maxSerach);
		int totalsize = hits.totalHits;
		pager = new Pager(inputPageno, 0, totalsize);
		articlelist=new ArrayList<ArticleEntity>(pager.getPagesize());
		for (int i = pager.getStart(); (i < pager.getEnd()) && (i < totalsize); i++) {
			ScoreDoc sdoc = hits.scoreDocs[i];
			Document doc = searcher.doc(sdoc.doc);
			long articleid = Long.parseLong(doc.get("articleid"));
			ArticleEntity article=articleMapper.get(articleid);
			if (article==null) {
				// todo
			} else
				articlelist.add(article);
		}
		return SUCCESS;
	}
}
