package org.kbs.archiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.kbs.archiver.persistence.*;
import org.kbs.library.AttachmentData;
import org.kbs.library.Converter;
import org.kbs.library.FileHeaderInfo;
import org.kbs.library.FileHeaderSet;
import org.kbs.library.TwoObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;

public class ArchiverBoardImpl implements Callable<Integer>, Runnable {

	private ApplicationContext ctx;
	// private BoardEntity board;
	private String boardbasedir;
	private IndexWriter writer;
	private Document document;
	private Field articleidField;
	private Field bodyField;
	private Field subjectField;
	private HashSet<String> filenameset=new HashSet<String>();
	private boolean testonly = true;
	private BlockingQueue<BoardEntity> workqueue;

	public ArchiverBoardImpl(ApplicationContext ctx,
			BlockingQueue<BoardEntity> workqueue, String boardbasedir,
			IndexWriter writer) {
		this.ctx = ctx;
		this.workqueue = workqueue;
		this.boardbasedir = boardbasedir;
		this.writer = writer;
		document = new Document();
		articleidField = new Field("articleid", "", Field.Store.YES,
				Field.Index.NO);
		bodyField = new Field("body", "", Field.Store.NO, Field.Index.ANALYZED);
		subjectField = new Field("subject", "", Field.Store.NO,
				Field.Index.ANALYZED);
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

		if (!testonly) {
			threadseq.setReadonly(false);
			articleseq.setReadonly(false);
			attachmentseq.setReadonly(false);
		}
		ArrayList<FileHeaderInfo> articlelist;
		FileHeaderSet fhset = new FileHeaderSet();
		Logger logger = Logger.getLogger(ArchiverBoardImpl.class);
		logger.info(new Date(System.currentTimeMillis()) + " Archiver "
				+ board.getName() + " start up:");
		long totalattchmentsize = 0;

		// SqlSessionTemplate sqlsession = (SqlSessionTemplate) ctx
		// .getBean("sqlSession");

		ThreadMapper threadMapper = (ThreadMapper) ctx
				.getBean("threadMapper");
		ArticleMapper articleMapper = (ArticleMapper) ctx
				.getBean("articleMapper");
		List<String> filenames = articleMapper.getFilenamesByBoard(board
				.getBoardid());
		filenameset.clear();
		for (String f : filenames) {
			filenameset.add(f);
		}

		String boardpath = boardbasedir + "/" + board.getName() + "/";
		ArrayList<FileHeaderInfo> dirlist;
		if (!(new java.io.File(boardpath + ".DIR").exists())) {
			logger.error(boardpath + ".DIR no exists");
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
		logger.info("load " + board.getName() + ":" + boardpath + ".DIR:"
				+ articlelist.size() + "/" + dirlist.size());
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
			logger.info("add " + board.getName() + "/" + fh.getFilename());

			// lucene索引
			if (!testonly && !board.isIshidden()) {
				articleidField.setValue(new Long(article.getArticleid())
						.toString());
				bodyField.setValue(body.getFirst());
				subjectField.setValue(article.getSubject());
				document.add(articleidField);
				document.add(bodyField);
				document.add(subjectField);
				writer.addDocument(document);
				document.removeField("articleid");
				document.removeField("body");
				document.removeField("subject");
			}
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
				logger.debug("old thread:" + fh.getGroupid());
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
						logger.error(
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
				// TODO:fix insert body
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("articleid", new Long(article.getArticleid()));
				map.put("body", body.getFirst());
				if (!testonly)
					batchsqlsession
							.insert("org.kbs.archiver.persistence.ArticleBodyMapper.addMap",
									map);
			} catch (Exception e) {
				logger.error("insert into article " + article.toString(), e);
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

		if (articlelist.size() > 0) { // 更新board表的lastid,threads
			board.setLastarticleid(articlelist.get(articlelist.size() - 1)
					.getArticleid());
			board.setThreads(threads.size());
			board.setArticles(articlelist.size());
			if (!testonly) {
				batchsqlsession
						.update("org.kbs.archiver.persistence.BoardMapper.updateLast",
								board);
				batchsqlsession.flushStatements();
			}
		}

		logger.info(new Date(System.currentTimeMillis()) + " Archiver "
				+ board.getName() + " end:add " + articlelist.size()
				+ " articles " + threads.size() + " threads,update "
				+ oldthreads.size() + "threads");
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

		// TODO:处理更多异常情况，比如articleid重置，版面合并id重置等
		int count = 0;
		for (FileHeaderInfo fh : dirlist) {
			// if (fh.getArticleid() > board.getLastarticleid()) { // new data
			count++;
			if (fh.getFilename().isEmpty()) {
				System.out.println("invalid fileheader-board:"
						+ board.getName() + " index:" + count);
			} else if (!filenameset.contains(fh.getFilename())) {
				articlelist.add(fh);
			}
		}
		return articlelist;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isTestonly() {
		return testonly;
	}

	public void setTestonly(boolean testonly) {
		this.testonly = testonly;
	}

}
