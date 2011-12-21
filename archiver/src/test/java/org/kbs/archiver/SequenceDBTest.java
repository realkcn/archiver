package org.kbs.archiver;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.kbs.library.InitTest;
import org.kbs.library.SimpleException;

public class SequenceDBTest {

	@Before
	public void initialize() {
		InitTest.init();
	}
	
	@Test
	public void test() {
		SequenceDB db=new SequenceDB();
		try {
			db.init();
			for (int i=0;i<1000;i++) {
				int value;
				value=(int)(Math.random()*10002)+1;
				db.put("hello", value);
				assertEquals(db.get("hello"),value);
			}
			db.shutdown();
		} catch (SimpleException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getInfo()+":"+e.getDetail());
			e.printStackTrace();
			fail();
		}
	}

}
