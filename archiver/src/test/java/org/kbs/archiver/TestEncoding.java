package org.kbs.archiver;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kbs.archiver.ArchiverBoardImpl;
import org.kbs.archiver.BoardEntity;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.archiver.persistence.ThreadMapper;
import org.kbs.library.InitTest;


public class TestEncoding {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	//	InitTest.init();
	}

	@Test
	public void test() {
		System.out.println("中文");
	}

}

