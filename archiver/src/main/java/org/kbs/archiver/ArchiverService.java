package org.kbs.archiver;

import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.kbs.archiver.lucene.SolrUpdater;
import org.kbs.archiver.persistence.*;
import org.kbs.library.BoardHeaderInfo;
import org.kbs.library.DBTools;
import org.kbs.library.FileHeaderInfo;
import org.kbs.library.FileHeaderSet;
import org.kbs.library.SimpleException;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArchiverService extends TimerTask {

	private static final Logger LOG = LoggerFactory
			.getLogger(ArchiverService.class);
	java.util.concurrent.atomic.AtomicBoolean running = new java.util.concurrent.atomic.AtomicBoolean();

	private ApplicationContext ctx;
	private String boardBaseDir = null;
	private boolean testonly = false;
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
		try {
			Thread[] workerthread = new Thread[nThreads];
			ArrayBlockingQueue<BoardEntity> workqueue = new ArrayBlockingQueue<BoardEntity>(
					20);
			for (int i = 0; i < nThreads; i++) {
				ArchiverBoardImpl worker = new ArchiverBoardImpl(ctx,
						workqueue, boardBaseDir);
				worker.setTestonly(testonly);
				worker.setUseLastUpdate(useLastUpdate);
				workerthread[i] = new Thread(worker);
				workerthread[i].start();
			}
			try {
				for (BoardEntity theBoard : boards) {
					if (!theBoard.isIgnored())
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
				LOG.error("run", e);
			}
			// 结束
            // 更新solr
            SolrUpdater solrUpdater=new SolrUpdater();
            solrUpdater.init(ctx);
            solrUpdater.deltaImport();

			running.set(false);
		} catch (SimpleException e) {
			LOG.error("run", e);
		} finally {

		}
	}

	/**
	 * 从.BOARDS更新board表
	 * 
	 * @param filename
	 *            .BOARDS文件目录. examples: /home/bbs/.BOARDS
	 */
	public synchronized void updateBoardDB(String filename) {
		List<BoardHeaderInfo> bhset = BoardHeaderInfo.readDotBoard(filename);
		if (bhset == null) {
			LOG.error("Can't found {}", filename);
			return;
		}
		BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
		List<BoardEntity> boards = boardMapper.selectAll();
		CachedSequence boardSeq = (CachedSequence) ctx.getBean("boardSeq");
		boardSeq.setReadonly(testonly);
		for (BoardHeaderInfo bh : bhset) {
			if (!bh.isGroup()) {// 非目录版面才处理
				BoardEntity board = boardMapper.getByName(bh.getFilename());
				if (board == null) {
					board = new BoardEntity(bh);
					if (!testonly) {
						board.setBoardid(boardSeq.next());
						boardMapper.insert(board);
					}
					LOG.info("add board:" + board.getBoardid() + " name:"
							+ board.getName() + " cname:" + board.getCname()
							+ " hidden:" + board.isIshidden());
				} else {
					board.set(bh);
					if (!testonly)
						boardMapper.update(board);
					LOG.info("update board:" + board.getBoardid() + " name:"
							+ board.getName() + " cname:" + board.getCname()
							+ " hidden:" + board.isIshidden());
				}
			}
		}
		// 检查已经归档的版面是否还存在
		for (BoardEntity board : boards) {
			boolean found = false;
			// 隐藏版面不检查
			if (board.isIshidden() == true)
				continue;
			for (BoardHeaderInfo bh : bhset) {
				if (bh.getFilename().equals(board.getName())) {
					found = true;
					break;
				}
			}
			if (!found) {
				// 已经归档的版面找不到了，设置为隐藏
				if (!testonly) {
					board.setIshidden(true);
					board.setIgnored(true);
					boardMapper.update(board);
				}
				LOG.info("board {} deleted,set hidden", board.getName());
			}
		}
	}

	/**
	 * 设置版面是否归档
	 * 
	 * @param boardname
	 *            版面名字
	 * @param ignored
	 *            是否忽略
	 */
	public void ignoreBoard(String boardname, boolean ignored) {
		BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
		BoardEntity board = boardMapper.getByName(boardname);
		if (board == null) {
			LOG.error("board {} not found!", boardname);
			return;
		}
		if (!testonly) {
			board.setIgnored(ignored);
			boardMapper.update(board);
		}
	}

	public synchronized void deleteArticle(long articleid) {
		BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
		ThreadMapper threadMapper = (ThreadMapper) ctx.getBean("threadMapper");
        FrontPageMapper frontPageMapper = (FrontPageMapper) ctx.getBean("frontpageMapper");
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
                frontPageMapper.deleteByid(thread.getThreadid());
				if (board != null)
					board.setThreads(-1);
			}
		// 处理board
		if (board != null) {
			board.setArticles(-1);
			// System.out.println(board.toString());
			boardMapper.updateLast(board); // 其实这里和归档进程有竞争问题
		}
		// 处理索引
		SolrUpdater solrUpdater = new SolrUpdater();
		if (!solrUpdater.init()) {
			solrUpdater.delete(articleid);
			solrUpdater.commit();
		}
	}

	/**
	 * 删除一个主题
	 * 
	 * @param threadid
	 *            主题id
	 */
	public synchronized void deleteThread(long threadid) {
		BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
		ThreadMapper threadMapper = (ThreadMapper) ctx.getBean("threadMapper");
        FrontPageMapper frontPageMapper = (FrontPageMapper) ctx.getBean("frontpageMapper");
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
            frontPageMapper.deleteByid(thread.getThreadid());
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

	/**
	 * 通过文件名修正一个版面的originid字段，并重新生成thread信息。
	 * TODO: 通过标题修复
	 * 
	 * @param boardname
	 *            版面名
	 * @param force
	 *            是否通过标题强行修复而不是读.DIR
	 * @throws SimpleException
	 */
	public void fixOriginid(String boardname, boolean force)
			throws SimpleException {
		//Connection conn=null;
		try {
			ArticleMapper articleMapper = (ArticleMapper) ctx
					.getBean("articleMapper");
			BoardMapper boardMapper = (BoardMapper) ctx.getBean("boardMapper");
			ThreadMapper threadMapper = (ThreadMapper) ctx
					.getBean("threadMapper");
			BoardEntity board = boardMapper.getByName(boardname);
			if (board == null) {
				throw new SimpleException("not such board " + boardname);
			}
			if (!testonly) {
				articleMapper.resetOriginidByBoard(board.getBoardid());
			}
			/*
			// 获得所有文章的文件名,数据量太大，用forward only的
			Hashtable<String,Long> archivedData=new Hashtable<String,Long>();
			DataSource ds = (DataSource) ctx.getBean("dataSource");
			conn = ds.getConnection();
			PreparedStatement stmt = conn.prepareStatement(
					"select articleid,filename from article where boardid=? order by posttime asc",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setLong(0, board.getBoardid());
			ResultSet rs=stmt.executeQuery();
			// 需要重新生成originid
			while (rs.next()) {
				archivedData.put(rs.getString(1), rs.getLong(0));
			}
			*/
			//获得版面现在的.DIR信息
			String boardpath = boardBaseDir + "/" + board.getName() + "/";
			ArrayList<FileHeaderInfo> dirlist;
			if (!(new java.io.File(boardpath + ".DIR").exists())) {
				LOG.warn("{} .DIR no exists",boardpath);
				return;
			}
			FileHeaderSet fhset = new FileHeaderSet();
			dirlist = fhset.readBBSDir(boardpath + ".DIR");
			
			for (FileHeaderInfo fh:dirlist) {
				//设定每一个article的originid
				if (!testonly)
					articleMapper.updateOriginid(fh.articleid,board.getBoardid(),fh.filename);
				LOG.debug("set {} originid={}",fh.filename,fh.articleid);
			}
			if (!testonly)
				threadMapper.resetOriginidByBoard(board.getBoardid());
		} finally {
//			DBTools.closeQuietly(conn);
		}

	}
}
