package org.kbs.archiver.action;

import java.util.List;

import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.ThreadEntity;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.archiver.persistence.ThreadMapper;

import com.opensymphony.xwork2.ActionSupport;

public class ListArticle extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3341508236419947469L;
	List<ArticleEntity> articlelist;

	private ThreadMapper threadMapper;
	private long threadid;
	private ThreadEntity thread;
	private String tid;

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public long getThreadid() {
		return threadid;
	}

	public void setThreadid(long threadid) {
		this.threadid = threadid;
	}

	private int pageno;
	private int pagesize;

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public ThreadMapper getThreadMapper() {
		return threadMapper;
	}

	public void setThreadMapper(ThreadMapper threadMapper) {
		this.threadMapper = threadMapper;
	}

	public int getPageno() {
		return pageno;
	}

	public void setPageno(int pageno) {
		this.pageno = pageno;
	}

	public int getTotalpage() {
		return totalpage;
	}

	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	private int totalpage;

	public List<ArticleEntity> getArticlelist() {
		return articlelist;
	}

	public void setArticlelist(List<ArticleEntity> articlelist) {
		this.articlelist = articlelist;
	}

	private ArticleMapper articleMapper;

	public ArticleMapper getArticleMapper() {
		return articleMapper;
	}

	public void setArticleMapper(ArticleMapper articleMapper) {
		this.articleMapper = articleMapper;
	}

	// action for get articles on thread
	public String getByThread() throws Exception {
		if (pagesize == 0)
			pagesize = 20;
		if (pageno == 0)
			pageno = 1;
		thread = threadMapper.getByEncodingUrl(tid);
		if (thread == null)
			return ERROR;
		totalpage = thread.getArticlenumber() / pagesize
				+ ((thread.getArticlenumber() % pagesize > 0) ? 1 : 0);
		if ((pageno - 1) * pagesize > thread.getArticlenumber()) {
			pageno = thread.getArticlenumber() / pagesize + 1;
		} else if ((pageno - 1) * pagesize == thread.getArticlenumber()) {
			pageno = thread.getArticlenumber() / pagesize;
		}
		articlelist = articleMapper.getByThreadPerPage(thread.getThreadid(), (pageno - 1)
				* pagesize, pagesize);
		// WebApplicationContextUtils.getWebApplicationContext(this.)
		return SUCCESS;
	}

	public ThreadEntity getThread() {
		return thread;
	}

	public void setThread(ThreadEntity thread) {
		this.thread = thread;
	}
}
