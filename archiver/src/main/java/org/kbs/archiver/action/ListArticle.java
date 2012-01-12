package org.kbs.archiver.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.ThreadEntity;
import org.kbs.archiver.persistence.ArticleBodyMapper;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.AttachmentMapper;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.archiver.persistence.ThreadMapper;
import org.kbs.archiver.util.Pager;
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
	private String encodingURL;
	private BoardMapper boardMapper;
	private BoardEntity board;
	private Pager pager;
	
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


	public int getPagesize() {
		return pager.getPagesize();
	}

	public ThreadMapper getThreadMapper() {
		return threadMapper;
	}

	public void setThreadMapper(ThreadMapper threadMapper) {
		this.threadMapper = threadMapper;
	}

	public int getPageno() {
		return pager.getPageno();
	}

	private int inputPageno;
	public void setPageno(int no) {
		this.inputPageno=no;
	}
	public int getTotalpage() {
		return pager.getTotalpage();
	}

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
	private ArticleBodyMapper articleBodyMapper;
	public ArticleBodyMapper getArticleBodyMapper() {
		return articleBodyMapper;
	}

	public void setArticleBodyMapper(ArticleBodyMapper articleBodyMapper) {
		this.articleBodyMapper = articleBodyMapper;
	}

	public AttachmentMapper getAttachmentMapper() {
		return attachmentMapper;
	}

	public void setAttachmentMapper(AttachmentMapper attachmentMapper) {
		this.attachmentMapper = attachmentMapper;
	}


	public ArticleMapper getArticleMapper() {
		return articleMapper;
	}

	public void setArticleMapper(ArticleMapper articleMapper) {
		this.articleMapper = articleMapper;
	}

	public String getArticle() throws Exception {
		if (encodingURL==null) {
			addActionError("错误的id参数");
			return ERROR;
		}
		ArticleEntity article=articleMapper.getByEncodingUrl(encodingURL);
		if (article==null)  {
			addActionError("找不到id为"+encodingURL+"的文章");
			return ERROR;
		}
		article.setBody(articleBodyMapper.get(article.getArticleid()));
		articlelist=new ArrayList<ArticleEntity>(1);
		articlelist.add(article);
		dealAttachment();
		return SUCCESS;
	}
	
	// action for get articles on thread
	public String getByThread() throws Exception {
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
		int totalsize = thread.getArticlenumber();
		if (totalsize==0) {
			this.addActionError("该主题没有文章。");
			return ERROR;
		}
		pager=new Pager(inputPageno, 0, totalsize);
		articlelist = articleMapper.getByThreadPerPage(thread.getThreadid(), pager.getStart(), pager.getPagesize());
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
		return pager.getTotalsize();
	}

	public String getByAuthor() throws Exception {
//		System.out.println(getAuthor());
		int totalsize = articleMapper.countByAuthor(getAuthor());
		if (totalsize==0) {
			this.addActionError(getAuthor()+"没有发表过文章。");
			return ERROR;
		}
		pager=new Pager(inputPageno, 0, totalsize);
//		System.out.println(String.format("getAuthor:%s total:%d totalpage:%d pageno:%d",getAuthor(),totalsize,totalpage,pageno));
		articlelist = articleMapper.getByAuthorPerPage(getAuthor(), pager.getStart(), pager.getPagesize());
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

	/**
	 * @return the encodingurl
	 */
	public String getEncodingURL() {
		return encodingURL;
	}

	/**
	 * @param encodingurl the encodingurl to set
	 */
	public void setEncodingURL(String encodingurl) {
		this.encodingURL = encodingurl;
	}
}
