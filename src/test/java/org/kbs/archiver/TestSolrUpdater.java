package org.kbs.archiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kbs.archiver.lucene.SolrUpdater;
import org.kbs.library.InitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext-test.xml"})
public class TestSolrUpdater {
	static SolrUpdater solrUpdater;

	@BeforeClass
	public static void initialize() {
		solrUpdater = new SolrUpdater();
	}

	@Test
	public void test() {
		/*
		long articleid=Long.MAX_VALUE;
		String magic = "abcdefghi09876";
		assertEquals(solrUpdater.init(InitTest.getAppContext()), true);
		System.out.println("add article");
		ArticleEntity article = new ArticleEntity();
		article.setAuthor("author" + magic);
		article.setSubject("subject" + magic);
		article.setPosttime(new Date());
		article.setArticleid(articleid);
		solrUpdater.addArticle(article, "body" + magic);
		solrUpdater.commit();

		Properties config = (Properties) InitTest.getAppContext()
				.getBean("configproperties");
		try {
			SolrServer solr = new CommonsHttpSolrServer(
					config.getProperty("solrurl"));
		    ModifiableSolrParams params = new ModifiableSolrParams();   
		    params.set("fl","articleid");   
		    params.set("q", "body"+magic);   
		    params.set("start", 0);   
		    params.set("rows", 20);   
		    QueryResponse response = solr.query(params);
		    SolrDocumentList docs = response.getResults();
		    assertEquals(docs.getNumFound(),1);
		    for (SolrDocument doc : docs) {
				long queryarticleid = (Long)doc.getFieldValue("articleid");
				assertEquals(queryarticleid,articleid);
		    }
		    solrUpdater.delete(articleid);
		    solrUpdater.commit();
		    response = solr.query(params);
		    docs = response.getResults();
		    assertEquals(docs.getNumFound(),0);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail("open solr server failed:"+config.getProperty("solrurl"));
		} catch (SolrServerException e) {
			e.printStackTrace();
			fail("solr server failed");
		}
		*/
	}
}
