package org.kbs.archiver;

import java.util.Date;

public class ThreadEntity {
	private long boardid;
	private String subject;
	private Date posttime;
	private int articlenumber;
	private String author;
	private String lastreply;
	private Date lastposttime;
	private long threadid;
	private String encodingurl;
	
	public long getBoardid() {
		return boardid;
	}
	public void setBoardid(long boardid) {
		this.boardid = boardid;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Date getPosttime() {
		return posttime;
	}
	public void setPosttime(Date posttime) {
		this.posttime = posttime;
	}
	public int getArticlenumber() {
		return articlenumber;
	}
	public void setArticlenumber(int articlenumber) {
		this.articlenumber = articlenumber;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getLastreply() {
		return lastreply;
	}
	public void setLastreply(String lastreply) {
		this.lastreply = lastreply;
	}
	public Date getLastposttime() {
		return lastposttime;
	}
	public void setLastposttime(Date lastposttime) {
		this.lastposttime = lastposttime;
	}
	public long getThreadid() {
		return threadid;
	}
	public void setThreadid(long threadid) {
		this.threadid = threadid;
	}
	public String getEncodingurl() {
		return encodingurl;
	}
	public void setEncodingurl(String encodingurl) {
		this.encodingurl = encodingurl;
	}

}
