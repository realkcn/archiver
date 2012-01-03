package org.kbs.archiver;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kbs.archiver.persistence.ArticleBodyBDB;
import org.kbs.library.SimpleException;

public class TestArticleBodyBDB {
	static ArticleBodyBDB bdb=new ArticleBodyBDB();
@BeforeClass
public static void setUp() {
	try {
		bdb.init();
	} catch (SimpleException e) {
		e.printStackTrace();
	}
}

@Test
public void testBDB() {
	long [] data=new long[100];
	for (int i=0;i<100;i++) {
		data[i]=(long)(Math.random()*100000);
		bdb.put(data[i], "ffd中文"+data[i]+"#$#@$#@呵呵");
	}
	for (int i=0;i<100;i++) {
		assertEquals(bdb.get(data[i]),"ffd中文"+data[i]+"#$#@$#@呵呵");
	}
	for (int i=0;i<100;i++) {
		bdb.delete(data[i]);
	}
	for (int i=0;i<100;i++) {
		assertEquals(bdb.get(data[i]),null);
	}
}

@AfterClass
public static void shutdown() {
	bdb.shutdown();
}
}
