package org.kbs.archiver;

import java.util.List;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import org.kbs.archiver.lucene.SolrUpdater;
import org.kbs.archiver.persistence.ArticleBodyMapper;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.AttachmentMapper;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.archiver.persistence.ThreadMapper;
import org.kbs.library.BoardHeaderInfo;
import org.kbs.library.SimpleException;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.wltea.analyzer.lucene.IKAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArchiverService extends TimerTask {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverService.class);
	java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean();

	private ApplicationContext ctx;
	private String boardBaseDir = null;
	private boolean testonly=false;
	private boolean useLastUpdate;

	public boolean isTestonly() {
		return testonly;
	}

	public void setTestonly(boolean testonly) {
		this.testonly = testonly;
	}

	public boolean isUseLastUpdate() {
		return useLastUpdate;
	}

	public void setUseLastUpdate(boolean useLastUpdate) {
		this.useLastUpdate = useLastUpdate;
	}

	public ArchiverService(ApplicationContext ctx) {
		this.ctx = ctx;
	}

	public void setBoardBaseDir(String basedir) {
		boardBaseDir = basedir;
	}

	@Override
	public void run() {
		if (running.getAndSet(true)) // 避免重入
			return;
		// 扫描board表并开始遍历更新
		BoardMapper boardmapper = (BoardMapper) ctx.getBean("boardMapper");
		List<BoardEntity> boards = boardmapper.selectAll();
		int nThreads = 0;
		Properties config = (Properties) ctx.getBean("configproperties");
		if (config.get("workerthreads") != null)
			nThreads = Integer.parseInt((String) config.get("workerthreads"));
		if (nThreads <= 0)
			nThreads = 4;

		if (boardBaseDir == null)
			boardBaseDir = config.getProperty("boarddir");
		// LinkedList<Callable<Integer>> tasks=new
		// LinkedList<Callable<Integer>>();
		SolrUpdater solrUpdater=new SolrUpdater();
		try {
			if (!solrUpdater.init(ctx))
				throw new SimpleException("Can't open solr");
			Thread[] workerthread = new Thread[nThreads];
			ArrayBlockingQueue<BoardEntity> workqueue = new ArrayBlockingQueue<BoardEntity>(
					20);
			for (int i = 0; i < nThreads; i++) {
				ArchiverBoardImpl worker = new ArchiverBoardImpl(ctx,
						workqueue, boardBaseDir, solrUpdater);
				worker.setTestonly(testonly);
				worker.setUseLastUpdate(useLastUpdate);
				workerthread[i] = new Thread(worker);
				workerthread[i].start();
			}
			try {
				for (BoardEntity theBoard : boards) {
					workqueue.put(theBoard);
				}
				// 设置线程结束标志
				BoardEntity board = new BoardEntity();
				board.setBoardid(-1);
				for (int i = 0; i < nThreads; i++) {
					workqueue.put(board);
				}
				for (int i = 0; i < nThreads; i++) {
					workerthread[i].join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// 结束
			solrUpdater.commit();
			running.set(false);
		} catch (SimpleException e) {
			e.printStackTrace();
		} finally {

		}
	}

	public synchronized void updateBoardDB(String filename) {
		List<BoardHeaderInfo> bhset = BoardHeaderInfo.readDotBoard(filename);
		if (bhset == null) {
			LOG.error("Can't found {}",filename);
			return;
		}
		CachedSequence boardSeq = (CachedSequence) ctx.getBean("boardSeq");
		boardSeq.setReadonly(testonly);
		for (BoardHeaderInfo bh : bhset) {
			if (!bh.isGroup()) {// 非目录版面才处理
				BoardMapper boardMapper = (BoardMapper) ctx
						.getBean("boardMapper");
				BoardEntity board = boardMapper.getByName(bh.getFilename());
				if (board == null) {
					board = new BoardEntity(bh);
					board.setBoardid(boardSeq.next());
					if (!testonly)
						boardMapper.insert(board);
					LOG.info(
							"add board:" + board.getBoardid() + " name:"
									+ board.getName() + " cname:"
									+ board.getCname());
				} else {
					board.set(bh);
					if (!testonly)
						boardMapper.update(board);
					LOG.info(
							"update board:" + board.getBoardid() + " name:"
									+ board.getName() + " cname:"
									+ board.getCname());
				}
			}
		}
	}

	public synchronized void deleteArticle(long articleid) {
		BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
		ThreadMapper threadMapper = (ThreadMapper) ctx.getBean("threadMapper");
		ArticleMapper articleMapper = (ArticleMapper) ctx
				.getBean("articleMapper");
		ArticleBodyMapper articleBodyMapper = (ArticleBodyMapper) ctx
				.getBean("articleBodyMapper");
		AttachmentMapper attachmentMapper = (AttachmentMapper) ctx
				.getBean("attachmentMapper");

		ArticleEntity article = articleMapper.get(articleid);
		ThreadEntity thread = threadMapper.get(article.getThreadid());
		BoardEntity board = boardMapper.get(article.getBoardid());
		if (article.getAttachment() != 0) {
			attachmentMapper.deleteByArticle(article.getArticleid());
		}
		articleMapper.delete(articleid);
		articleBodyMapper.delete(articleid);
		// 处理thread
		if (board != null)
			board.setThreads(0);
		if (thread != null)
			if (thread.getArticlenumber() > 1) {// 是否处理第一篇是该文章的情况？
				thread.setArticlenumber(thread.getArticlenumber() - 1);
				threadMapper.update(thread);
			} else {
				threadMapper.delete(thread.getThreadid());
				if (board != null)
					board.setThreads(-1);
			}
		// 处理board
		if (board != null) {
			board.setArticles(-1);
			// System.out.println(board.toString());
			boardMapper.updateLast(board); // 其实这里和归档进程有竞争问题
		}
		//处理索引
		SolrUpdater solrUpdater=new SolrUpdater();
		if (!solrUpdater.init()) {
			solrUpdater.delete(articleid);
			solrUpdater.commit();
		}
	}

	public synchronized void deleteThread(long threadid) {
		BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
		ThreadMapper threadMapper = (ThreadMapper) ctx.getBean("threadMapper");
		ArticleMapper articleMapper = (ArticleMapper) ctx
				.getBean("articleMapper");
		ArticleBodyMapper articleBodyMapper = (ArticleBodyMapper) ctx
				.getBean("articleBodyMapper");
		AttachmentMapper attachmentMapper = (AttachmentMapper) ctx
				.getBean("attachmentMapper");

		List<ArticleEntity> articlelist = articleMapper.getByThreadPerPage(
				threadid, 0, -1);
		ThreadEntity thread = threadMapper.get(threadid);
		BoardEntity board = boardMapper.get(thread.getBoardid());
		for (ArticleEntity article : articlelist) {
			if (article.getAttachment() != 0) {
				attachmentMapper.deleteByArticle(article.getArticleid());
			}
			articleMapper.delete(article.getArticleid());
			articleBodyMapper.delete(article.getArticleid());
		}
		// 处理thread
		if (thread != null) {
			threadMapper.delete(thread.getThreadid());
			if (board != null)
				board.setThreads(-1);
		}
		// 处理board
		if (board != null) {
			board.setArticles(-articlelist.size());
			boardMapper.updateLast(board); // 其实这里和归档进程有竞争问题
		}
	}

	public void deleteByAuthor(String author) {
		ArticleMapper articleMapper = (ArticleMapper) ctx
				.getBean("articleMapper");
		List<ArticleEntity> articlelist = articleMapper.getByAuthorPerPage(
				author, 0, -1);
		for (ArticleEntity article : articlelist) {
			deleteArticle(article.getArticleid());
		}
	}
}
