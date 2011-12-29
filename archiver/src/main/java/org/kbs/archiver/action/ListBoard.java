package org.kbs.archiver.action;

import java.util.List;

import org.kbs.archiver.*;
import org.kbs.archiver.persistence.*;
import org.springframework.web.context.support.WebApplicationContextUtils;

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
		boardlist=boardMapper.selectAll();
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
