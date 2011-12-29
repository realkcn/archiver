package org.kbs.archiver;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.InitTest;
import org.kbs.library.SimpleException;

public class TestSequenceDB {

	private CachedSequence threadseq;
	private CachedSequence articleseq;
	@BeforeClass
	public void initialize() {
		InitTest.init();
		threadseq=(CachedSequence) InitTest.getAppContext().getBean("threadseq");
		articleseq=(CachedSequence) InitTest.getAppContext().getBean("articleseq");
	}
	
	@Test
	public void test() {
		for (long i=0;i<50;i++) {
			long value;
			value=(long)(Math.random()*10002)+1;
			threadseq.setValue(value);
			threadseq.flush();
			threadseq.setValue(0);
			threadseq.load();
			assertEquals(threadseq.getValue(),value);
			articleseq.setValue(value);
			articleseq.flush();
			articleseq.setValue(0);
			articleseq.load();
			assertEquals(articleseq.getValue(),value);
		}
	}
}
