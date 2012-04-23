package org.kbs.archiver;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.DBTools;
import org.kbs.library.InitTest;
import org.kbs.sso.principal.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext-test.xml"})
public class TestBoard {
	@Autowired
    private static BoardMapper boardMapper; 

	@Test
	public void testBoardDB() {
        AttributePrincipal principal=new AttributePrincipal("",new HashMap<String, Object>());
		/*
		BoardEntity board = new BoardEntity();
		board.setName("hello");
		board.setBoardid(10241);
		board.setArticles((int) (Math.random() * 10002) + 1);
		board.setThreads((int) (Math.random() * 10002) + 1);
		board.setIshidden(true);

		// board.flushtoDB();
		boardMapper.deleteByName(board.getName());
		boardMapper.insert(board);
		BoardEntity board2 = boardMapper.getByName(board.getName());
		assertEquals("hello", board2.getName());
		assertEquals(board.getBoardid(), board2.getBoardid());
		assertEquals(board.getArticles(), board2.getArticles());
		assertEquals(board.getThreads(), board2.getThreads());
		assertEquals(true, board2.isIshidden());

		boardMapper.deleteByName("hello");
		// Board board2=new Board();
		// board2.loadBoardFromDB("hello");
	*/
	}

}
