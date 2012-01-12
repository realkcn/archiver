package org.kbs.archiver.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.apache.struts2.ServletActionContext;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.lucene.Tools;
import org.kbs.archiver.persistence.ArticleBodyMapper;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.util.Pager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wltea.analyzer.lucene.*;

import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class SearchArticle extends ActionSupport {
	private String subject = null;
	private String body=null;
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	private ArticleMapper articleMapper = null;
	private ArticleBodyMapper articleBodyMapper = null;
	public void setArticleBodyMapper(ArticleBodyMapper articleBodyMapper) {
		this.articleBodyMapper = articleBodyMapper;
	}

	public void setArticleMapper(ArticleMapper articleMapper) {
		this.articleMapper = articleMapper;
	}

	private static IndexReader indexReader = null;
	private ArrayList<ArticleEntity> articlelist = null;

	public ArrayList<ArticleEntity> getArticlelist() {
		return articlelist;
	}
	
	private Pager pager;
	private int inputPageno;
	public void setPageno(int no) {
		this.inputPageno=no;
	}
	public int getTotalpage() {
		return pager.getTotalpage();
	}
	public int getTotalsize() {
		return pager.getTotalsize();
	}
	public int getPagesize() {
		return pager.getPagesize();
	}
	public int getPageno() {
		return pager.getPageno();
	}

	private static long checkTime = new Date().getTime();

	private static void CheckIndex() throws IOException {
		long now = new Date().getTime();
		synchronized (SearchArticle.class) {
			if (now - checkTime > 15 * 60 * 1000) {
				IndexReader newReader = IndexReader.openIfChanged(indexReader,
						true);
				if (newReader != null) {
					indexReader = newReader;
				}
			}
		}
	}

	public String Search() throws Exception {
		if (((subject==null)||subject.isEmpty()) &&((body==null)||body.isEmpty()))
			return SUCCESS;
		try {
			int maxSearch = 10000;
			if (inputPageno*20>maxSearch) {
				addActionError("别看那么多结果，服务器抗议！");
				return ERROR;
			}
			if (indexReader == null) {
				synchronized (SearchArticle.class) {
					if (indexReader == null) {
						WebApplicationContext webApplicationContext = WebApplicationContextUtils
								.getRequiredWebApplicationContext(ServletActionContext
										.getServletContext());
						indexReader = IndexReader
								.open(FSDirectory
										.open(new File(
												Tools.getLucenceDirectory(webApplicationContext))),
										true);
						// readonly indexreader
					}
				}
			}
			CheckIndex();
			IndexSearcher searcher = new IndexSearcher(indexReader);
			searcher.setSimilarity(new IKSimilarity());
			// Query query = IKQueryParser.parse("subject", subject);
			Analyzer analyzer = new IKAnalyzer();
			Query query;
			if ((subject==null)||(body==null)||subject.isEmpty()||body.isEmpty()) {
				if ((subject==null)||subject.isEmpty()) {
					QueryParser queryParser = new QueryParser(Version.LUCENE_35,
							"body", analyzer);
					query = queryParser.parse(body);
				} else  {
					QueryParser queryParser = new QueryParser(Version.LUCENE_35,
							"subject", analyzer);
					query = queryParser.parse(subject);
				}
			} else {
				BooleanClause.Occur[] flags = new BooleanClause.Occur[] {
					     BooleanClause.Occur.MUST, BooleanClause.Occur.MUST};
				query=MultiFieldQueryParser.parse(
						Version.LUCENE_35,
						new String [] {subject,body},
						new String [] {"subject","body"},
						flags,
						analyzer
						);
				
			}
			TopDocs hits = searcher.search(query, maxSearch);
			int totalsize = hits.totalHits;
			pager = new Pager(inputPageno, 0, totalsize);
			articlelist = new ArrayList<ArticleEntity>(pager.getPagesize());
			//System.out.println("get from "+pager.getStart()+" to "+pager.getEnd());
			for (int i = pager.getStart(); (i < pager.getEnd())
					&& (i < hits.scoreDocs.length ); i++) {
				ScoreDoc sdoc = hits.scoreDocs[i];
				Document doc = searcher.doc(sdoc.doc);
				long articleid = Long.parseLong(doc.get("articleid"));
				ArticleEntity article = articleMapper.get(articleid);
				if (article == null) {
					// todo
				} else {
					article.setBody(articleBodyMapper.get(articleid));
					articlelist.add(article);
				}
			}
			searcher.close();
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e.toString());
			return ERROR;
		}
	}
}
