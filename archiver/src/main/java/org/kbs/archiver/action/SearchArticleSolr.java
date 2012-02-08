package org.kbs.archiver.action;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Properties;

import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.persistence.ArticleBodyMapper;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.util.Pager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionSupport;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.struts2.ServletActionContext;

@SuppressWarnings("serial")
public class SearchArticleSolr extends ActionSupport {
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
	
	public String Search() throws Exception {
		if (((subject==null)||subject.isEmpty()) &&((body==null)||body.isEmpty()))
			return SUCCESS;
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext
						.getServletContext());
		Properties config = (Properties) webApplicationContext.getBean("configproperties");
	    SolrServer solr = new CommonsHttpSolrServer(config.getProperty("solrurl"));

	    // http://localhost:8983/solr/spellCheckCompRH?q=epod&spellcheck=on&spellcheck.build=true
	    
	    String querystring=new String();
	    if (subject!=null) {
	    		querystring="subject:\""+subject+"\" ";
	    }
	    if (body!=null) {
	    		querystring+="\""+body+"\"";
	    }

	    ModifiableSolrParams params = new ModifiableSolrParams();   
	    params.set("fl","articleid");   
	    params.set("q", querystring);   
	    params.set("start", inputPageno*20);   
	    params.set("rows", 20);   
	    QueryResponse response = solr.query(params);
	    SolrDocumentList docs = response.getResults();
	    pager = new Pager(inputPageno, 0, docs.getNumFound());
		articlelist = new ArrayList<ArticleEntity>(pager.getPagesize());
	    for (SolrDocument doc : docs) {
			long articleid = (Long)doc.getFieldValue("articleid");
			ArticleEntity article = articleMapper.get(articleid);
			if (article == null) {
				// todo
			} else {
				article.setBody(articleBodyMapper.get(articleid));
				articlelist.add(article);
			}
	    }
	    return SUCCESS;
	}
}
