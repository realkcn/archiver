package org.kbs.archiver.action;


import java.util.List;

import org.kbs.archiver.*;
import org.kbs.archiver.persistence.*;

import com.opensymphony.xwork2.ActionSupport;

public class ListThread extends ActionSupport {


	private static final long serialVersionUID = 7184950339787196119L;
	List<ThreadEntity> threadlist;
	private BoardMapper boardMapper;
	private ThreadMapper threadMapper;
	private long boardid;
	private int pageno;
	private int totalpage;
	public int getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	private BoardEntity board;
	
	public BoardEntity getBoard() {
		return board;
	}
	public void setBoard(BoardEntity board) {
		this.board = board;
	}
	public int getPageno() {
		return pageno;
	}
	public void setPageno(int pageno) {
		this.pageno = pageno;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	private int pagesize;

	public List<ThreadEntity> getThreadlist() {
		return threadlist;
	}
	public void setThreadlist(List<ThreadEntity> threadlist) {
		this.threadlist = threadlist;
	}
	public long getBoardid() {
		return boardid;
	}
	public void setBoardid(long boardid) {
		this.boardid = boardid;
	}
	public BoardMapper getBoardMapper() {
		return boardMapper;
	}
	public void setBoardMapper(BoardMapper boardMapper) {
		this.boardMapper = boardMapper;
	}
	public ThreadMapper getThreadMapper() {
		return threadMapper;
	}
	public void setThreadMapper(ThreadMapper threadMapper) {
		this.threadMapper = threadMapper;
	}

	@Override
	public String execute() throws Exception {
		if (pagesize==0)
			pagesize=20;
		if (pageno==0)
			pageno=1;
		board=boardMapper.get(boardid);
		if (board==null) {
			return ERROR;
		}
//		WebApplicationContext.
		totalpage=board.getThreads()/pagesize+((board.getThreads()%pagesize>0)?1:0);
		if ((pageno-1)*pagesize>board.getThreads()) {
			pageno=board.getThreads()/pagesize+1;
		} else if ((pageno-1)*pagesize==board.getThreads()) {
			pageno=board.getThreads()/pagesize;
		}
		threadlist=threadMapper.getByBoardPerPage(boardid, (pageno-1)*pagesize, pagesize);
//		WebApplicationContextUtils.getWebApplicationContext(this.)
		return SUCCESS;
	}
}
