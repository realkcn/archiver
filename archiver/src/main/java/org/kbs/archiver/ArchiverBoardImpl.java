package org.kbs.archiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.kbs.archiver.persistence.ThreadMapper;
import org.kbs.library.AttachmentData;
import org.kbs.library.Converter;
import org.kbs.library.FileHeaderInfo;
import org.kbs.library.FileHeaderSet;
import org.kbs.library.TwoObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;

public class ArchiverBoardImpl implements Callable<Integer>, Runnable {

	private ApplicationContext ctx;
	private BoardEntity board;
	private String boardbasedir;

	public ArchiverBoardImpl(ApplicationContext ctx, BoardEntity board,String boardbasedir) {
		this.ctx = ctx;
		this.board = board;
		this.boardbasedir=boardbasedir;
	}

	public Integer call() throws Exception {

		SqlSessionTemplate batchsqlsession = (SqlSessionTemplate) ctx
				.getBean("batchSqlSession");
		CachedSequence threadseq = (CachedSequence) ctx.getBean("threadSeq");
		CachedSequence articleseq = (CachedSequence) ctx.getBean("articleSeq");
		CachedSequence attachmentseq = (CachedSequence) ctx.getBean("attachmentSeq");
		ArrayList<FileHeaderInfo> articlelist;
		FileHeaderSet fhset = new FileHeaderSet();
		Logger logger=Logger.getLogger(ArchiverBoardImpl.class);
		logger.info(new Date(System.currentTimeMillis())+" Archiver "+board.getName()+" start up:");
		long totalattchmentsize=0;

//		SqlSessionTemplate sqlsession = (SqlSessionTemplate) ctx
//				.getBean("sqlSession");

		ThreadMapper threadMapper = (ThreadMapper) ctx.getBean("threadMapper");

		String boardpath = boardbasedir+"/" + board.getName() +"/";
		ArrayList<FileHeaderInfo> dirlist;
		if (!(new java.io.File(boardpath + ".DIR").exists())) {
			logger.error(boardpath + ".DIR no exists");
			return 0;
		}
		dirlist=fhset.readBBSDir(boardpath + ".DIR");
		/*
		 * ArrayList<FileHeaderInfo> deletedlist = new
		 * ArrayList<FileHeaderInfo>(); if (new java.io.File(boardpath +
		 * "/.DELETED").exists()) { deletedlist = fhset.readBBSDir(boardpath +
		 * "/.DELETED"); }
		 */
		// 生成需要处理的list
//		batchsqlsession.execute(new SqlMapClientCallback() {
		articlelist = gennewlist(dirlist);
		logger.info("load "+board.getName()+":"+boardpath + ".DIR:"+articlelist.size()+"/"+dirlist.size());
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
			//!board.isIshidden());
			TwoObject<String, ArrayList<AttachmentData>> body = fh
					.getBody(boardpath);
			article.setArticleid(articleseq.next());
			article.setEncodingurl(Converter.randomEncodingfromlong(article
					.getArticleid()));
			article.setBody(body.getFirst());
			article.setAttachment(body.getSecond().size());// 附件个数

			logger.debug("deal:"+article.toString());
//			System.out.println("中文");
//			System.exit(0);

			// 处理thread,需要填写threadid,并看看是否要生成新的thread
			ThreadEntity thread;
			if (fh.getGroupid() > board.getLastarticleid()) {
				thread = threads.get(fh.getGroupid());
				if (thread != null) {
					thread.addArticle(fh);
				} else {
					// 需要生成新的thread
					thread = ThreadEntity.newThread(threadseq.next(), board,
							article);
				}
				threads.put(fh.getGroupid(), thread);
			} else { // 处理已经存在的thread
				logger.debug("old thread:"+fh.getGroupid());
				thread = oldthreads.get(fh.getGroupid());
				if (thread == null) {
					// 从数据库中获得原来的thread信息
					thread = threadMapper.getByOriginId(board.getBoardid(),fh.getGroupid());
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

			if (body.getSecond().size()!=0) {
				int order=0;
				for (AttachmentData data:body.getSecond()) {
					AttachmentEntity attachment=new AttachmentEntity();
					attachment.setArticleid(article.getArticleid());
					attachment.setAttachmentid(attachmentseq.next());
					attachment.setData(data.getData());
					attachment.setEncodingurl(Converter.randomEncodingfromlong(attachment.getAttachmentid()));
					attachment.setName(data.getName());
					attachment.setOrder(order);
					attachment.setBoardid(board.getBoardid());
					order++;
					totalattchmentsize+=attachment.getData().length;
					if (totalattchmentsize>90*1024*1024) {//大于90M flush一次.....
						batchsqlsession.flushStatements();
						totalattchmentsize=attachment.getData().length;
					}
					batchsqlsession.insert(
							"org.kbs.archiver.persistence.AttachmentMapper.insert",
							attachment);
				}
			}
			// 插入新记录
			batchsqlsession.insert(
					"org.kbs.archiver.persistence.ArticleMapper.insert",
					article);
		}

		// 加入新的thread
		Set<Map.Entry<Long, ThreadEntity>> threadset = threads.entrySet();
		for (Map.Entry<Long, ThreadEntity> value : threadset) {
			batchsqlsession.insert(
					"org.kbs.archiver.persistence.ThreadMapper.insert",
					value.getValue());
		}

		// 更新已经存在的thread
		threadset = oldthreads.entrySet();
		for (Map.Entry<Long, ThreadEntity> value : threadset) {
			batchsqlsession.update(
					"org.kbs.archiver.persistence.ThreadMapper.update",
					value.getValue());
		}

		if (articlelist.size() > 0) { // 更新board表的lastid,threads
			board.setLastarticleid(articlelist.get(articlelist.size() - 1)
					.getArticleid());
			board.setThreads(threads.size());
			board.setArticles(articlelist.size());
			batchsqlsession.update(
					"org.kbs.archiver.persistence.BoardMapper.updateLast",
					board);
			batchsqlsession.flushStatements();
		}

		logger.info(new Date(System.currentTimeMillis())+" Archiver "+board.getName()+" end:add "+articlelist.size()+" articles "+threads.size()+" threads,update "+oldthreads.size()+"threads");
		return articlelist.size();
	}

	private ArrayList<FileHeaderInfo> gennewlist(
			ArrayList<FileHeaderInfo> dirlist)
	// ArrayList<FileHeaderInfo> deletedlist
	{
		ArrayList<FileHeaderInfo> articlelist = new ArrayList<FileHeaderInfo>();
		// Date now=new Date(System.currentTimeMillis());

		// TODO:处理更多异常情况，比如articleid重置，版面合并id重置等
		for (FileHeaderInfo fh : dirlist) {
			if (fh.getArticleid() > board.getLastarticleid()) { // new data
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

}
