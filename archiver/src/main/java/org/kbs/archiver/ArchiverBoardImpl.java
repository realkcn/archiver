package org.kbs.archiver;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.kbs.library.AttachmentData;
import org.kbs.library.Converter;
import org.kbs.library.FileHeaderInfo;
import org.kbs.library.FileHeaderSet;
import org.kbs.library.TwoObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.ApplicationContext;

public class ArchiverBoardImpl implements Callable<Integer> {

	private ApplicationContext ctx;
	private BoardEntity board;

	public ArchiverBoardImpl(ApplicationContext ctx, BoardEntity board) {
		this.ctx = ctx;
		this.board = board;
	}

	public Integer call() throws Exception {

		SqlSessionTemplate batchsqlsession = (SqlSessionTemplate) ctx
				.getBean("batchSqlSession");
		ArrayList<FileHeaderInfo> articlelist;
		try {
			FileHeaderSet fhset = new FileHeaderSet();

			SqlSessionTemplate sqlsession = (SqlSessionTemplate) ctx
					.getBean("sqlSession");

			String boardpath;
			boardpath = ((Properties) ctx.getBean("configproperties"))
					.getProperty("boarddir");
			boardpath += "/" + board.getName();
			ArrayList<FileHeaderInfo> dirlist = new ArrayList<FileHeaderInfo>();
			long lastdirid = 0;
			if (new java.io.File(boardpath + "/.DELETED").exists()) {
				fhset.readBBSDir(boardpath + "/.DIR");
				lastdirid = dirlist.get(dirlist.size() - 1).getArticleid();
			}
			/*
			ArrayList<FileHeaderInfo> deletedlist = new ArrayList<FileHeaderInfo>();
			if (new java.io.File(boardpath + "/.DELETED").exists()) {
				deletedlist = fhset.readBBSDir(boardpath + "/.DELETED");
			}
			*/
			//生成需要处理的list
			articlelist = gennewlist(dirlist);
			HashMap<Long,ThreadEntity> threads=new HashMap<Long,ThreadEntity>();
			ArrayList<ThreadEntity> oldthreads=new ArrayList<ThreadEntity>();
			
			for (FileHeaderInfo fh : articlelist) {
				ArticleEntity article = new ArticleEntity(fh);
				article.setBoardid(board.getBoardid());
				TwoObject<String, ArrayList<AttachmentData>> body = fh
						.getBody(boardpath);
				article.setAttachment(body.getSecond().size());// 附件个数

				article.setBody(body.getFirst());
				//TODO:处理附件存储
				
				// 处理thread,需要填写threadid,并看看是否要生成新的thread
				if (fh.getGroupid()>board.getLastarticleid()) {
					ThreadEntity thread=threads.get(fh.getGroupid());
					if (thread!=null) {
						thread.setArticlenumber(thread.getArticlenumber()+1);
					} else {
						//需要生成新的thread
						long threadid=0; //TODO: thread id 的生成问题
						thread=new ThreadEntity();
						thread.setThreadid(threadid); 
						thread.setArticlenumber(1);//考虑最后再插入
						thread.setAuthor(article.getAuthor());
						thread.setBoardid(board.getBoardid());
						thread.setLastposttime(article.getPosttime());
						thread.setLastreply(article.getAuthor());
						thread.setPosttime(article.getPosttime());
						thread.setSubject(article.getSubject());
						thread.setEncodingurl(Converter.randomEncodingfromlong(threadid));
//						thread.setThreadid()
					}
					threads.put(fh.getGroupid(),thread);
				} else { //处理已经存在的thread
					//TODO
				}
				
				//插入新记录
				batchsqlsession.insert(
						"org.kbs.archiver.persistence.ArticleMapper.insert",
						article);
			}
			
			//加入新的thread
			Set<Map.Entry<Long,ThreadEntity>> threadset=threads.entrySet();
			for (Map.Entry<Long,ThreadEntity> value:threadset) {
				batchsqlsession.insert(
						"org.kbs.archiver.persistence.ThreadMapper.insert",
						value.getValue());
			}
			
			if (articlelist.size()>0) { //更新
				board.setLastarticleid(articlelist.get(articlelist.size()-1).getArticleid());
				batchsqlsession.update("org.kbs.archiver.persistence.BoardMapper.updateLast", board);
			}
			batchsqlsession.commit();
		} finally {
			batchsqlsession.close();
		}
		
		return articlelist.size();
	}

	private ArrayList<FileHeaderInfo> gennewlist(ArrayList<FileHeaderInfo> dirlist)
//			ArrayList<FileHeaderInfo> deletedlist 
	{
		ArrayList<FileHeaderInfo> articlelist=new ArrayList<FileHeaderInfo>();
		//Date now=new Date(System.currentTimeMillis());
		
		// TODO:处理更多异常情况，比如articleid重置，版面合并id重置等
		for (FileHeaderInfo fh : articlelist) {
			if (fh.getArticleid() > board.getLastarticleid()) { // new data
				articlelist.add(fh);
			}
		}
		return articlelist;
	}

}
