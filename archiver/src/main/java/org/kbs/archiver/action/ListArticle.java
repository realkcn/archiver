package org.kbs.archiver.action;

import java.util.List;

import org.apache.log4j.Logger;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.ThreadEntity;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.AttachmentMapper;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.archiver.persistence.ThreadMapper;
import org.kbs.library.AttachmentData;


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
	private String author;

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
	public BoardMapper getBoardMapper() {
		return boardMapper;
	}
	public void setBoardMapper(BoardMapper boardMapper) {
		this.boardMapper = boardMapper;
	}
	private ArticleMapper articleMapper;
	private AttachmentMapper attachmentMapper;
	public AttachmentMapper getAttachmentMapper() {
		return attachmentMapper;
	}

	public void setAttachmentMapper(AttachmentMapper attachmentMapper) {
		this.attachmentMapper = attachmentMapper;
	}

	private BoardMapper boardMapper;
	private BoardEntity board;
	private int totalsize;

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
		if (thread == null) {
			this.addActionError("找不到该主题。");
			return ERROR;
		}
		board=boardMapper.get(thread.getBoardid());
		if (board==null) {
			Logger.getLogger(ListArticle.class).warn("found thread without board:"+threadid+" on boardid:"+thread.getBoardid());
			this.addActionError("找不到该主题。");
			return ERROR;
		}
		if (board.isIshidden()) {
			this.addActionError("找不到该主题。");
			return ERROR;
		}
		totalsize = thread.getArticlenumber();
		if (totalsize==0) {
			this.addActionError("该主题没有文章。");
			return ERROR;
		}
		totalpage = totalsize/ pagesize	+ ((totalsize % pagesize > 0) ? 1 : 0);
		if ((pageno - 1) * pagesize > totalsize) {
			pageno = totalsize / pagesize + 1;
		} else if ((pageno - 1) * pagesize == totalsize) {
			pageno = totalsize / pagesize;
		}
		articlelist = articleMapper.getByThreadPerPage(thread.getThreadid(), (pageno - 1)
				* pagesize, pagesize);
		dealAttachment();
		// WebApplicationContextUtils.getWebApplicationContext(this.)
		
		return SUCCESS;
	}

	public void dealAttachment() {
		for (ArticleEntity article:articlelist) {
			if (article.getAttachment()>0) {
				article.setAttachments(attachmentMapper.getByArticle(article.getArticleid()));
			}
		}
	}
	
	public int getTotalsize() {
		return totalsize;
	}

	public void setTotalsize(int totalsize) {
		this.totalsize = totalsize;
	}

	public String getByAuthor() throws Exception {
//		System.out.println(getAuthor());
		if (pagesize == 0)
			pagesize = 20;
		if (pageno == 0)
			pageno = 1;
		totalsize = articleMapper.countByAuthor(getAuthor());
		if (totalsize==0) {
			this.addActionError(getAuthor()+"没有发表过文章。");
			return ERROR;
		}
		totalpage = totalsize/ pagesize	+ ((totalsize % pagesize > 0) ? 1 : 0);
		if ((pageno - 1) * pagesize > totalsize) {
			pageno = totalsize / pagesize + 1;
		} else if ((pageno - 1) * pagesize == totalsize) {
			pageno = totalsize / pagesize;
		}
//		System.out.println(String.format("getAuthor:%s total:%d totalpage:%d pageno:%d",getAuthor(),totalsize,totalpage,pageno));
		articlelist = articleMapper.getByAuthorPerPage(getAuthor(), (pageno - 1)
				* pagesize, pagesize);
		// WebApplicationContextUtils.getWebApplicationContext(this.)
		return SUCCESS;
	}
	public BoardEntity getBoard() {
		return board;
	}

	public void setBoard(BoardEntity board) {
		this.board = board;
	}

	public ThreadEntity getThread() {
		return thread;
	}

	public void setThread(ThreadEntity thread) {
		this.thread = thread;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}
}
