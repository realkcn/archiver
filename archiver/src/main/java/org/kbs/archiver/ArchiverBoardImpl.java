package org.kbs.archiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.kbs.archiver.persistence.*;
import org.kbs.library.AttachmentData;
import org.kbs.library.Converter;
import org.kbs.library.FileHeaderInfo;
import org.kbs.library.FileHeaderSet;
import org.kbs.library.SimpleException;
import org.kbs.library.TwoObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;

public class ArchiverBoardImpl implements Callable<Integer>, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ArchiverBoardImpl.class);
	private ApplicationContext ctx;
	// private BoardEntity board;
	private String boardbasedir;
	private HashSet<String> filenameset=new HashSet<String>();
	private boolean testonly = false;
	private BlockingQueue<BoardEntity> workqueue;
	private boolean useLastUpdate=true;

	public ArchiverBoardImpl(ApplicationContext ctx,
			BlockingQueue<BoardEntity> workqueue, String boardbasedir) throws SimpleException {
		this.ctx = ctx;
		this.workqueue = workqueue;
		this.boardbasedir = boardbasedir;
	}

	public void work(BoardEntity board) throws Exception {
		SqlSessionTemplate batchsqlsession = (SqlSessionTemplate) ctx
				.getBean("batchSqlSession");
		CachedSequence threadseq = (CachedSequence) ctx
				.getBean("threadSeq");
		CachedSequence articleseq = (CachedSequence) ctx
				.getBean("articleSeq");
		CachedSequence attachmentseq = (CachedSequence) ctx
				.getBean("attachmentSeq");
		ThreadMapper threadMapper = (ThreadMapper) ctx
				.getBean("threadMapper");
		ArticleMapper articleMapper = (ArticleMapper) ctx
				.getBean("articleMapper");
		if (!testonly) {
			threadseq.setReadonly(false);
			articleseq.setReadonly(false);
			attachmentseq.setReadonly(false);
		}
		
		ArrayList<FileHeaderInfo> articlelist;
		FileHeaderSet fhset = new FileHeaderSet();
//		LOG.info("{} Archiver {} start up:",new Date(System.currentTimeMillis()),board.getName());
		long totalattchmentsize = 0;

		// SqlSessionTemplate sqlsession = (SqlSessionTemplate) ctx
		// .getBean("sqlSession");

		if (!useLastUpdate) {
		List<String> filenames = articleMapper.getFilenamesByBoard(board
				.getBoardid());
		filenameset.clear();
		for (String f : filenames) {
			filenameset.add(f);
		}
		}

		String boardpath = boardbasedir + "/" + board.getName() + "/";
		ArrayList<FileHeaderInfo> dirlist;
		if (!(new java.io.File(boardpath + ".DIR").exists())) {
			LOG.warn("{} .DIR no exists",boardpath);
			return;
		}
		dirlist = fhset.readBBSDir(boardpath + ".DIR");
		/*
		 * ArrayList<FileHeaderInfo> deletedlist = new
		 * ArrayList<FileHeaderInfo>(); if (new java.io.File(boardpath +
		 * "/.DELETED").exists()) { deletedlist = fhset.readBBSDir(boardpath
		 * + "/.DELETED"); }
		 */
		// 生成需要处理的list
		// batchsqlsession.execute(new SqlMapClientCallback() {
		articlelist = gennewlist(board,dirlist);
		LOG.debug("load {}:{}/.DIR:{}/{}",new Object[] {board.getName(),boardpath,articlelist.size(),dirlist.size()});
		HashMap<Long, ThreadEntity> threads = new HashMap<Long, ThreadEntity>();
		HashMap<Long, ThreadEntity> oldthreads = new HashMap<Long, ThreadEntity>();

		for (FileHeaderInfo fh : articlelist) {
			ArticleEntity article = new ArticleEntity();
			article.setOriginid(fh.getArticleid());
			article.setAuthor(fh.getOwner());
			article.setFilename(fh.getFilename());
			article.setPosttime(fh.getPosttime());
			article.setSubject(fh.getTitle());
			article.setReplyid(fh.getReplyid());
			article.setBoardid(board.getBoardid());
			article.setIsvisible(true);
			// !board.isIshidden());
			TwoObject<String, ArrayList<AttachmentData>> body = fh
					.getBody(boardpath);
			article.setArticleid(articleseq.next());
			article.setEncodingurl(Converter.randomEncodingfromlong(article
					.getArticleid()));
			article.setAttachment(body.getSecond().size());// 附件个数

			// 处理正文
			// article.setBody(body.getFirst());
			// System.out.println("deal:" + article.toString());
			// logger.debug("deal:" + article.toString());
			LOG.debug("add {}/{}",board.getName(),fh.getFilename());

			// 处理thread,需要填写threadid,并看看是否要生成新的thread
			ThreadEntity thread;
			if (fh.getGroupid() > board.getLastarticleid()) {
				thread = threads.get(fh.getGroupid());
				if (thread != null) {
					thread.addArticle(fh);
				} else {
					// 需要生成新的thread
					thread = ThreadEntity.newThread(threadseq.next(),
							board, article);
				}
				threads.put(fh.getGroupid(), thread);
			} else { // 处理已经存在的thread
				LOG.debug("old thread:{}",fh.getGroupid());
				thread = oldthreads.get(fh.getGroupid());
				if (thread == null) {
					// 从数据库中获得原来的thread信息
					thread = threadMapper.getByOriginId(board.getBoardid(),
							fh.getGroupid());
					if (thread == null) {
						thread = ThreadEntity.newThread(threadseq.next(),
								board, article);
						threads.put(thread.getOriginid(), thread);
					} else {
						// 加入到oldthread缓存中。
						thread.addArticle(fh);
						oldthreads.put(fh.getGroupid(), thread);
					}
				} else
					thread.addArticle(fh);
			}
			article.setThreadid(thread.getThreadid());

			if (body.getSecond().size() != 0) {
				int order = 0;
				for (AttachmentData data : body.getSecond()) {
					AttachmentEntity attachment = new AttachmentEntity();
					attachment.setArticleid(article.getArticleid());
					attachment.setAttachmentid(attachmentseq.next());
					attachment.setData(data.getData());
					attachment.setEncodingurl(Converter
							.randomEncodingfromlong(attachment
									.getAttachmentid()));
					attachment.setName(data.getName());
					attachment.setOrder(order);
					attachment.setBoardid(board.getBoardid());
					order++;
					totalattchmentsize += attachment.getData().length;
					if (totalattchmentsize > 90 * 1024 * 1024) {// 大于90M
																// flush一次.....
						if (!testonly)
							batchsqlsession.flushStatements();
						totalattchmentsize = attachment.getData().length;
					}
					/*
					 * System.out.println(String.format(
					 * "insert into attachment name:%s id:%d aid:%d order:%d data:%d url:%s"
					 * , attachment.getName(), attachment.getAttachmentid(),
					 * attachment.getArticleid(), attachment.getOrder(),
					 * attachment.getData().length,
					 * attachment.getEncodingurl()));
					 */
					try {
						if (!testonly)
							batchsqlsession
									.insert("org.kbs.archiver.persistence.AttachmentMapper.insert",
											attachment);
					} catch (Exception e) {
						LOG.error(
								String.format(
										"insert into attachment name:%s id:%d aid:%d order:%d data:%d url:%s",
										attachment.getName(),
										attachment.getAttachmentid(),
										attachment.getArticleid(),
										attachment.getOrder(),
										attachment.getData().length,
										attachment.getEncodingurl()), e);
					}
				}
			}
			// 插入新记录
			try {
				if (!testonly)
					batchsqlsession
							.insert("org.kbs.archiver.persistence.ArticleMapper.insert",
									article);
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("articleid", new Long(article.getArticleid()));
				map.put("body", body.getFirst());
				if (!testonly)
					batchsqlsession
							.insert("org.kbs.archiver.persistence.ArticleBodyMapper.addMap",
									map);
			} catch (Exception e) {
				LOG.error("insert into article " + article.toString(), e);
			}
		}

		// 加入新的thread
		Set<Map.Entry<Long, ThreadEntity>> threadset = threads.entrySet();
		for (Map.Entry<Long, ThreadEntity> value : threadset) {
			if (!testonly)
				batchsqlsession.insert(
						"org.kbs.archiver.persistence.ThreadMapper.insert",
						value.getValue());
		}

		// 更新已经存在的thread
		threadset = oldthreads.entrySet();
		for (Map.Entry<Long, ThreadEntity> value : threadset) {
			if (!testonly)
				batchsqlsession.update(
						"org.kbs.archiver.persistence.ThreadMapper.update",
						value.getValue());
		}

		if ((articlelist.size() > 0)) { // 更新board表的lastid,threads
			if (articlelist.size() > 0) {
				board.setLastarticleid(articlelist.get(articlelist.size() - 1)
					.getArticleid());
				board.setThreads(threads.size());
				board.setArticles(articlelist.size());
			} else {
				//不更新threads和articles计数
				board.setThreads(0);
				board.setArticles(0);
			}
			if (!testonly) {
				batchsqlsession
						.update("org.kbs.archiver.persistence.BoardMapper.updateLast",
								board);
				batchsqlsession.flushStatements();
			}
			LOG.info(" Archiver "
					+ board.getName() + " end:add " + articlelist.size()
					+ " articles " + threads.size() + " threads,update "
					+ oldthreads.size() + "threads");
		}
	}
	public Integer call() throws Exception {
		while (!Thread.interrupted()) {
			BoardEntity board=workqueue.take();
			if (board.getBoardid()==-1)
				break;
			work(board);
		}
		return 0;
	}

	private ArrayList<FileHeaderInfo> gennewlist(BoardEntity board,
			ArrayList<FileHeaderInfo> dirlist)
	// ArrayList<FileHeaderInfo> deletedlist
	{
		ArrayList<FileHeaderInfo> articlelist = new ArrayList<FileHeaderInfo>();
		// Date now=new Date(System.currentTimeMillis());

		int count = 0;
		for (FileHeaderInfo fh : dirlist) {
			count++;
			if (fh.getFilename().isEmpty()) {
				System.out.println("invalid fileheader-board:"
						+ board.getName() + " index:" + count);
			} else {
				if (!useLastUpdate) { 
					if (!filenameset.contains(fh.getFilename())) {
						articlelist.add(fh);
					}
				} else {
					if (fh.getArticleid() > board.getLastarticleid()) { // new data
						articlelist.add(fh);
					}
				}
			}
		}
		return articlelist;
	}

	@Override
	public void run() {
		try {
			call();
		} catch (Exception e) {
			LOG.error("run",e);
		}
	}

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

}
