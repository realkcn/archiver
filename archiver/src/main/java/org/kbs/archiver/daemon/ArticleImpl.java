package org.kbs.archiver.daemon;

import org.kbs.archiver.*;

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

public class ArticleImpl implements Callable<Integer>, Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleImpl.class);
    private ApplicationContext ctx;
    // private BoardEntity board;
    private String boardbasedir;
    private HashSet<String> filenameset = new HashSet<String>();
    private boolean testonly = false;
    private BlockingQueue< TwoObject<BoardEntity,FileHeaderInfo> > workqueue;
    private boolean useLastUpdate = true;

    public ArticleImpl(ApplicationContext ctx,
                             BlockingQueue< TwoObject<BoardEntity,FileHeaderInfo> > workqueue, String boardbasedir) throws SimpleException {
        this.ctx = ctx;
        this.workqueue = workqueue;
        this.boardbasedir = boardbasedir;
    }

    public void work( TwoObject<BoardEntity,FileHeaderInfo> param) throws Exception {
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

		//get board and fileheader from param
		BoardEntity board = param.getFirst();
		FileHeaderInfo fh = param.getSecond();

		//stiger:检查文件是否已经存在数据库
		ArticleEntity old_article = articleMapper.getByOriginId(
								board.getBoardid(), fh.getArticleid() );
		if (old_article != null) {
			//already exist
			LOG.warn("Article already exist: " + board.getBoardid() + ":" + fh.getArticleid() );
			return;
		}

		//stiger:这里没啥cache作用了，因为只是一篇article
		//用来记录是否要更新thread,其实不用HashMap了，懒得改了
        HashMap<Long, ThreadEntity> threads = new HashMap<Long, ThreadEntity>();
        HashMap<Long, ThreadEntity> oldthreads = new HashMap<Long, ThreadEntity>();

        String boardpath = boardbasedir + "/" + board.getName() + "/";

			//generate ArticleEntity from fh
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
            LOG.debug("add {}/{}", board.getName(), fh.getFilename());

            // 处理thread,需要填写threadid,并看看是否要生成新的thread
			// stiger:因为threads/oldthreads为空，所以简化处理
            ThreadEntity thread;
            if (fh.getGroupid() > board.getLastarticleid()) {
                    // 需要生成新的thread
                    thread = ThreadEntity.newThread(threadseq.next(),
                            board, article);
                threads.put(fh.getGroupid(), thread);
            } else { // 处理已经存在的thread
                LOG.debug("old thread:{}", fh.getGroupid());
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
            }
            article.setThreadid(thread.getThreadid());

			//处理附件
            if (body.getSecond().size() != 0) {
                int order = 0;
        		long totalattchmentsize = 0;
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

        // 加入新的thread
        Set<Map.Entry<Long, ThreadEntity>> threadset = threads.entrySet();
        for (Map.Entry<Long, ThreadEntity> value : threadset) {
            if (!testonly) {
                batchsqlsession.insert(
                        "org.kbs.archiver.persistence.ThreadMapper.insert",
                        value.getValue());
                if (!board.isIshidden()) {
                    value.getValue().setBoardname(board.getCname());
                    value.getValue().setGroupid(board.getGroupid());
                    batchsqlsession.insert(
                            "org.kbs.archiver.persistence.FrontPageMapper.addThread",
                            value.getValue());
                }
            }
        }

        // 更新已经存在的thread
        threadset = oldthreads.entrySet();
        for (Map.Entry<Long, ThreadEntity> value : threadset) {
            if (!testonly) {
                batchsqlsession.update(
                        "org.kbs.archiver.persistence.ThreadMapper.update",
                        value.getValue());
                if (!board.isIshidden())
                    batchsqlsession.update(
                            "org.kbs.archiver.persistence.FrontPageMapper.updateThread",
                            value.getValue());
            }
        }

		//TODO:
        //board.setLastdeleteid(dealDeleted(boardpath,board.getBoardid(),batchsqlsession,board.getLastdeleteid()));

        // 更新board表的lastid,threads,lastdelete
        board.setLastarticleid(article.getArticleid());
        board.setThreads(threads.size());
        board.setArticles(1);

        if (!testonly) {
            batchsqlsession
                    .update("org.kbs.archiver.persistence.BoardMapper.updateLast",
                            board);
            batchsqlsession.flushStatements();
        }

        LOG.warn("Add Article Done: " + board.getName() + ":" + article.getOriginid() );
    }

/*
    public long dealDeleted(String boardpath,long boardid,SqlSessionTemplate sqlSession,long lastdeleteid) {
        String deleteDirFile=boardpath+".DELETED";
        FileHeaderSet fhset = new FileHeaderSet();
        DeletedEntity deletedEntity=new DeletedEntity();

        if (new java.io.File(deleteDirFile).exists()) {
            ArrayList<FileHeaderInfo> deletelist = fhset.readBBSDir(deleteDirFile);
            boolean found=false;
            boolean loop=true;
            do {
                for (FileHeaderInfo fh:deletelist) {
                    if (!found) {
                        if (fh.getArticleid()==lastdeleteid) {
                            found=true;
                        }
                        continue;
                    }
//		System.out.println(fh.getArticleid()+"--"+lastdeleteid+"--"+loop+"--"+found);
                    deletedEntity.setOriginid(fh.getArticleid());
                    deletedEntity.setDeletetime(new Date());
                    int mark=fh.getTitle().lastIndexOf('-');
                    String deleteby;
                    if (mark!=-1) {
                        deleteby=fh.getTitle().substring(mark + 2);
                        mark=deleteby.indexOf(' ');
                        if (mark!=-1)
                            deleteby=deleteby.substring(0,mark-1);
                    } else deleteby="";
                    deletedEntity.setDeleteby(deleteby);
                    deletedEntity.setBoardid(boardid);
//                    System.out.println("  insert "+fh.toString());
                    if (!testonly)
                        sqlSession.insert(
                                "org.kbs.archiver.persistence.DeletedMapper.insert",
                                deletedEntity);
                    lastdeleteid=fh.getArticleid();
                }
                if (found) {
                    loop=false;
                } else {
                    found=true;
                }
            } while (loop);
        }
        return lastdeleteid;
    }
*/

    public Integer call() throws Exception {
        while (!Thread.interrupted()) {
            TwoObject<BoardEntity,FileHeaderInfo> param = workqueue.take();
            if (param.getFirst().getBoardid() == -1)
                break;
            work(param);
        }
        return 0;
    }
/*
    private ArrayList<FileHeaderInfo> gennewlist(BoardEntity board,
                                                 ArrayList<FileHeaderInfo> dirlist)
    // ArrayList<FileHeaderInfo> deletedlist
    {
        ArrayList<FileHeaderInfo> articlelist = new ArrayList<FileHeaderInfo>();
        // Date now=new Date(System.currentTimeMillis());

        int count = 0;
        for (FileHeaderInfo fh : dirlist) {
            count++;
            if (fh.getFilename().isEmpty()||(fh.getFilename().length()<8)) {
                System.out.println("invalid fileheader-board:"
                        + board.getName() + " index:" + count);
            } else {
                if (!useLastUpdate) {
                    String f;
                    if (fh.getFilename().charAt(1)=='/')
                        f=fh.getFilename().substring(4); //忽略开始的"x/M."
                    else
                        f=fh.getFilename().substring(2); //忽略开始的"M."

                    if (!filenameset.contains(f)) {
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
*/

    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            LOG.error("run", e);
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
