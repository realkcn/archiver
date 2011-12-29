package org.kbs.archiver;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kbs.archiver.ArchiverBoardImpl;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.InitTest;


public class TestArchiverBoardImpl  {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		InitTest.init();
	}

	@Test
	public void test() {
		/*
		BoardEntity theboard=new BoardEntity();
		theboard.setName("Progress");
		ArchiverBoardImpl worker=new ArchiverBoardImpl(InitTest.getAppContext(),theboard);
		try {
			assertEquals(221,worker.call().intValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("fail");
		}*/
	}

}
