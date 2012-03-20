package org.kbs.archiver.action;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.persistence.ArticleBodyMapper;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.AttachmentMapper;
import org.kbs.archiver.persistence.BoardMapper;
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
    private String author=null;
    private String start=null;
    private String end=null;
    private String boardname=null;
    private String sbyt=null; //sort by time
    private String aonly=null; //attachment only

    public String getSbyt() {
        return sbyt;
    }

    public void setSbyt(String sbyt) {
        this.sbyt = sbyt;
    }

    public String getAonly() {
        return aonly;
    }

    public void setAonly(String aonly) {
        this.aonly = aonly;
    }

    public String getBoardname() {
        return boardname;
    }

    public void setBoardname(String boardname) {
        this.boardname = boardname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

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
    private AttachmentMapper attachmentMapper = null;

    public void setAttachmentMapper(AttachmentMapper attachmentMapper) {
        this.attachmentMapper = attachmentMapper;
    }

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
	
    public String escapeString(String origin) {

        String ret=origin.replace("\\", "\\\\");
        return ret.replace("\"", "\\\"");
    }

    public void dealAttachment() {
        for (ArticleEntity article:articlelist) {
            if (article.getAttachment()>0) {
                article.setAttachments(attachmentMapper.getByArticle(article.getArticleid()));
            }
        }
    }

	public String Search() throws Exception {
		if (((subject==null)||subject.isEmpty()) &&((body==null)||body.isEmpty()) && ((author==null)||author.isEmpty())
                && ((boardname==null)||boardname.isEmpty())) {
            return SUCCESS;
        }
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getRequiredWebApplicationContext(ServletActionContext
						.getServletContext());
		Properties config = (Properties) webApplicationContext.getBean("configproperties");
	    SolrServer solr = new CommonsHttpSolrServer(config.getProperty("solrurl"));
	    // http://localhost:8983/solr/spellCheckCompRH?q=epod&spellcheck=on&spellcheck.build=true
	    
	    String querystring= "";
	    if ((subject!=null)&&(!subject.equals(""))) {
	    		querystring="subject:\""+escapeString(subject)+"\" ";
	    }
        if ((author!=null)&&!author.equals("")) {
            querystring+="author:\""+escapeString(author)+"\" ";
        }
	    if ((body!=null)&&!body.equals("")) {
	    		querystring+="body:\""+escapeString(body)+"\" ";
	    }
        if ((boardname!=null)&&!boardname.equals("")) {
            BoardMapper boardMapper=(BoardMapper)webApplicationContext.getBean("boardMapper");
            BoardEntity board=boardMapper.getByName(boardname);
            if (board==null) {
                addActionError("没有这个版");
                return ERROR;
            }
            querystring+="boardid:"+board.getBoardid()+" ";
        }
        Date startdate=null,enddate=null;
        SimpleDateFormat parser=new SimpleDateFormat("yyyyMMdd");
        if ((start!=null)&&!start.equals("")) {
            try {
                startdate=parser.parse(start);
            } catch (ParseException e) {
                addActionError("时间格式错误");
                return ERROR;
            }
        }
        if ((end!=null)&&!end.equals("")) {
            try {
                enddate=parser.parse(end);
            } catch (ParseException e) {
                addActionError("时间格式错误");
                return ERROR;
            }
        }
        if (startdate!=null||enddate!=null) {
            SimpleDateFormat formater=new SimpleDateFormat("yyyy-MM-dd");
            if (startdate!=null) {
                querystring+="posttime:["+formater.format(startdate)+"T00:00:00Z TO ";
            } else {
                querystring+="posttime:[* TO ";
            }
            if (enddate!=null) {
                querystring+=formater.format(enddate)+"T23:59:59Z] ";
            } else {
                querystring+="NOW] ";
            }
        }
        if (!StringUtils.isEmpty(aonly)) {
            querystring+="attachment:[1 TO *] ";
        }
	    ModifiableSolrParams params = new ModifiableSolrParams();
	    params.set("fl","articleid");   
	    params.set("q", querystring);
        if (inputPageno<=0)
            inputPageno=1;
	    params.set("start", (inputPageno-1)*20);
	    params.set("rows", 20);
        if (!StringUtils.isEmpty(sbyt)) {
            params.set("sort","posttime");
        }
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
        dealAttachment();
	    return SUCCESS;
	}
}
