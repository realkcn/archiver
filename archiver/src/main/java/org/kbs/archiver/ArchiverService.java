package org.kbs.archiver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.kbs.archiver.persistence.BoardMapper;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;

public class ArchiverService extends TimerTask {

	java.util.concurrent.atomic.AtomicBoolean running;
	
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
		List<Board> boards=boardmapper.selectAll();
		int nThreads=0;
		PropertiesFactoryBean config=(PropertiesFactoryBean) ctx.getBean("configproperties");
		try {
			nThreads=(Integer) config.getObject().get("workerthreads");
			if (nThreads<=0) nThreads=4;
		} catch (IOException e1) {
			
		}
		ThreadPoolExecutor exector=new ThreadPoolExecutor(nThreads, nThreads,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		LinkedList<Callable<Integer>> tasks=new LinkedList<Callable<Integer>>();
		for (Board theBoard : boards) {
			ArchiverBoardImpl worker;
			worker= new ArchiverBoardImpl(ctx,theBoard.getBoardid());
			tasks.add(worker);
		}
		try {
			exector.invokeAll(tasks);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//结束
		running.set(false);
	}

}
