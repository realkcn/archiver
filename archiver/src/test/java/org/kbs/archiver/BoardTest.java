package org.kbs.archiver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.kbs.library.DBTools;
import org.kbs.library.TestDataSource;

public class BoardTest {
	@Before
	public void setUp() throws Exception {
		DBTools.setDataSource(new TestDataSource());
	}
	
	@Test
	public void testBoardDB() {
		Board board=new Board();
		board.setName("hello");
		board.setBoardid(10);
		board.setArticles(1001);
		board.setThreads(1002);
		board.setIshidden(true);
		
		board.flushtoDB();
		
		Board board2=new Board();
		board2.loadBoardFromDB("hello");
		
		assertEquals("hello",board2.getName());
		assertEquals(10,board2.getBoardid());
		assertEquals(1001,board2.getArticles());
		assertEquals(1002,board2.getThreads());
		assertEquals(true,board2.isIshidden());
	}

}
