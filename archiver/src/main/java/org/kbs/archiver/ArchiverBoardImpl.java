package org.kbs.archiver;

import java.util.concurrent.Callable;

import org.springframework.context.ApplicationContext;

public class ArchiverBoardImpl implements Callable<Integer> {

	private ApplicationContext ctx;
	private int boardid;
	
	public ArchiverBoardImpl(ApplicationContext ctx,int boardid) {
		this.ctx=ctx;
		this.boardid=boardid;
	}

	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		
		return null;
	}

}
