package org.kbs.archiver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.DBTools;
import org.kbs.library.InitTest;
import org.kbs.library.TestDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class BoardTest {
	@Before
	public void setUp() throws Exception {
		InitTest.init();
		boardMapper=(BoardMapper) InitTest.getAppContext().getBean("boardMapper");
	}

	//@Autowired  
    private BoardMapper boardMapper; 

	@Test
	public void testBoardDB() {
		Board board = new Board();
		board.setName("hello");
		board.setBoardid((int) (Math.random() * 10002) + 1);
		board.setArticles((int) (Math.random() * 10002) + 1);
		board.setThreads((int) (Math.random() * 10002) + 1);
		board.setIshidden(true);

		// board.flushtoDB();
		boardMapper.updateByName(board);
		Board board2 = boardMapper.getByName(board.getName());
		assertEquals("hello", board2.getName());
		assertEquals(board.getBoardid(), board2.getBoardid());
		assertEquals(board.getArticles(), board2.getArticles());
		assertEquals(board.getThreads(), board2.getThreads());
		assertEquals(true, board2.isIshidden());

		// Board board2=new Board();
		// board2.loadBoardFromDB("hello");

	}

}
