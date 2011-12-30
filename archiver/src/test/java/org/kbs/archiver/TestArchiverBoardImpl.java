package org.kbs.archiver;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kbs.archiver.ArchiverBoardImpl;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.AttachmentMapper;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.archiver.persistence.ThreadMapper;
import org.kbs.library.InitTest;


public class TestArchiverBoardImpl  {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InitTest.init();
	}

	@Test
	public void test() {
		BoardEntity theboard=new BoardEntity();
		theboard.setName("MyPhoto");
		theboard.setCname("个人Show");
		theboard.setCname("水木进度");
		theboard.setLastarticleid(0);
		theboard.setBoardid(101);
		theboard.setThreads(0);
		theboard.setArticles(0);
		theboard.setIshidden(false);
		BoardMapper boardMapper=(BoardMapper) InitTest.getAppContext().getBean("boardMapper");
		boardMapper.deleteByName("Progress");
		boardMapper.insert(theboard);
		ArticleMapper articleMapper=(ArticleMapper) InitTest.getAppContext().getBean("articleMapper");
		articleMapper.deleteByBoard(theboard.getBoardid());
		ThreadMapper threadMapper=(ThreadMapper) InitTest.getAppContext().getBean("threadMapper");
		threadMapper.deleteByBoard(theboard.getBoardid());
		AttachmentMapper attachmentMapper=(AttachmentMapper) InitTest.getAppContext().getBean("attachmentMapper");
		attachmentMapper.deleteByBoard(theboard.getBoardid());
		ArchiverBoardImpl worker=new ArchiverBoardImpl(InitTest.getAppContext(),theboard);
		try {
//			assertEquals(221,worker.call().intValue());
			System.out.println("all record:"+worker.call().intValue());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
