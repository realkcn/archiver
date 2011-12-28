package org.kbs.archiver;

import java.util.Date;

import org.kbs.library.FileHeaderInfo;

public class ArticleEntity {
	private long boardid;
	private long threadid;
	private long articleid;
	private String author;
	private Date posttime;
	private int attachment;
	private String subject;
	private boolean isfirst;
	private boolean isvisible;
	private boolean isdelete;
	private long originid;
	private String filename;
	private long replyid;
	private String body;
	private String encodingurl;
	
	public String getEncodingurl() {
		return encodingurl;
	}
	public void setEncodingurl(String encodingurl) {
		this.encodingurl = encodingurl;
	}
	public void setArticleid(long articleid) {
		this.articleid = articleid;
	}
	public ArticleEntity(FileHeaderInfo fh) {
		this.originid=fh.getArticleid();
		this.author=fh.getOwner();
		this.setFilename(fh.getFilename());
		this.posttime=fh.getPosttime();
		this.subject=fh.getTitle();
		this.setReplyid(fh.getReplyid());
	}
	public long getBoardid() {
		return boardid;
	}
	public void setBoardid(long boardid) {
		this.boardid = boardid;
	}
	public long getThreadid() {
		return threadid;
	}
	public void setThreadid(long threadid) {
		this.threadid = threadid;
	}
	public long getArticleid() {
		return articleid;
	}
	public void setArticleid(int articleid) {
		this.articleid = articleid;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getPosttime() {
		return posttime;
	}
	public void setPosttime(Date posttime) {
		this.posttime = posttime;
	}
	public int getAttachment() {
		return attachment;
	}
	public void setAttachment(int attachment) {
		this.attachment = attachment;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public boolean isIsfirst() {
		return isfirst;
	}
	public void setIsfirst(boolean isfirst) {
		this.isfirst = isfirst;
	}
	public boolean isIsvisible() {
		return isvisible;
	}
	public void setIsvisible(boolean isvisible) {
		this.isvisible = isvisible;
	}
	public boolean isIsdelete() {
		return isdelete;
	}
	public void setIsdelete(boolean isdelete) {
		this.isdelete = isdelete;
	}
	public long getOriginid() {
		return originid;
	}
	public void setOriginid(long originid) {
		this.originid = originid;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getReplyid() {
		return replyid;
	}
	public void setReplyid(long replyid) {
		this.replyid = replyid;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}

}
