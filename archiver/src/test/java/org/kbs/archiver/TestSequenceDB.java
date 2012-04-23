package org.kbs.archiver;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kbs.archiver.persistence.BoardMapper;
import org.kbs.library.InitTest;
import org.kbs.library.SimpleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:applicationContext-test.xml"})
public class TestSequenceDB {

    @Autowired
    private CachedSequence threadseq;

    @Autowired
    private CachedSequence articleseq;

	@Test
	public void test() {
        threadseq.getValue();
        articleseq.getValue();
		/*
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
		*/
	}
}
