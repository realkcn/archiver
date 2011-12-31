package org.kbs.archiver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.BoardHeaderInfo;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;

public class ArchiverService extends TimerTask {

	java.util.concurrent.atomic.AtomicBoolean running=new java.util.concurrent.atomic.AtomicBoolean();
	
	private ApplicationContext ctx;
	
	public ArchiverService(ApplicationContext ctx) {
		this.ctx=ctx;
	}
	
	@Override
	public void run() {
		if (running.getAndSet(true)) //避免重入
			return;
		// 扫描board表并开始遍历更新
		BoardMapper boardmapper=(BoardMapper) ctx.getBean("boardMapper");
		List<BoardEntity> boards=boardmapper.selectAll();
		int nThreads=0;
		Properties config=(Properties) ctx.getBean("configproperties");
		if (config.get("workerthreads")!=null)
			nThreads=Integer.parseInt((String)config.get("workerthreads"));
		if (nThreads<=0) nThreads=4;

		String baordbasedir= config.getProperty("boarddir");
		ThreadPoolExecutor exector=new ThreadPoolExecutor(nThreads, nThreads,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
//		LinkedList<Callable<Integer>> tasks=new LinkedList<Callable<Integer>>();
		for (BoardEntity theBoard : boards) {
			ArchiverBoardImpl worker;
			worker= new ArchiverBoardImpl(ctx,theBoard,baordbasedir);
			exector.execute(worker);
		}
		exector.shutdown();
		/*
		try {
			exector.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		//结束
		running.set(false);
	}

	public synchronized void updateBoardDB(String filename) {
		List<BoardHeaderInfo> bhset=BoardHeaderInfo.readDotBoard(filename);
		for (BoardHeaderInfo bh:bhset) {
			BoardMapper boardMapper=(BoardMapper) ctx.getBean("boardMapper");
			CachedSequence boardSeq=(CachedSequence) ctx.getBean("boardSeq");
			BoardEntity board=boardMapper.getByName(bh.getFilename());
			if (board==null) {
				board=new BoardEntity(bh);
				board.setBoardid(boardSeq.next());
				boardMapper.insert(board);
				Logger.getLogger(ArchiverService.class).info("add board:"+board.getBoardid()+" name:"+board.getName()+" cname:"+board.getCname());
			} else {
				board.set(bh);
				boardMapper.update(board);
				Logger.getLogger(ArchiverService.class).info("update board:"+board.getBoardid()+" name:"+board.getName()+" cname:"+board.getCname());
			}
		}
	}
}
