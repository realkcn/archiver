package org.kbs.archiver.action;

import java.util.ArrayList;
import java.util.List;

import org.kbs.archiver.*;
import org.kbs.archiver.persistence.*;


import com.opensymphony.xwork2.ActionSupport;

public class ListBoard extends ActionSupport {


	private static final long serialVersionUID = 7184950339787196119L;
	List<BoardEntity> boardlist;
	private BoardMapper boardMapper;
	
	public List<BoardEntity> getBoardlist() {
		return boardlist;
	}
	public void setBoardlist(List<BoardEntity> boardlist) {
		this.boardlist = boardlist;
	}
	public ListBoard() {
		
	}
	@Override
	public String execute() throws Exception {
//		WebApplicationContext.
		/*
		List<BoardEntity>allboardlist=boardMapper.selectAll();
		boardlist=new ArrayList<BoardEntity>(); 
		for (BoardEntity board:allboardlist) {
			if (!board.isIshidden())
				boardlist.add(board);
		}
		*/
		boardlist=boardMapper.selectAllVisible();
//		WebApplicationContextUtils.getWebApplicationContext(this.)
		return SUCCESS;
	}
	public BoardMapper getBoardMapper() {
		return boardMapper;
	}
	public void setBoardMapper(BoardMapper boardMapper) {
		this.boardMapper = boardMapper;
	}

}
