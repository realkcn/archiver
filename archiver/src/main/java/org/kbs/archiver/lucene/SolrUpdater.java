package org.kbs.archiver.lucene;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.apache.struts2.ServletActionContext;
import org.kbs.archiver.ArticleEntity;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

//TODO: log error
public class SolrUpdater {
	private SolrServer solr;
	private SolrInputDocument document;
	
	public SolrUpdater() {
	}
	
	public boolean init() {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext
						.getServletContext());
		init(webApplicationContext);
		return true;
	}
	
	public boolean init(ApplicationContext appcontext) {

		Properties config = (Properties) appcontext.getBean("configproperties");
	    try {
			solr = new CommonsHttpSolrServer(config.getProperty("solrurl"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (solr==null) {
			Logger.getLogger(SolrUpdater.class).error("solr server initial failed:"+config.getProperty("solrurl"));
			return false;
		}
		document=new SolrInputDocument();
		return true;
	}
	public void addArticle(ArticleEntity article,String body) {
		if (solr==null) {
			return;
		}
		document.clear();
		document.addField("articleid", new Long(article.getArticleid()));
		document.addField("posttime", article.getPosttime());
		document.addField("author", article.getAuthor());
		document.addField("subject", article.getSubject());
		document.addField("body", body);
		try {
			solr.add(document);
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void commit() {
		if (solr!=null)
			try {
				solr.commit();
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void delete(long articleid) {
		if (solr!=null)
			try {
				solr.deleteById(new Long(articleid).toString());
			} catch (SolrServerException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
