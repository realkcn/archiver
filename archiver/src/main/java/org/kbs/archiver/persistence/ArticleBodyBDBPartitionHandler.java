package org.kbs.archiver.persistence;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.PartitionHandler;

public class ArticleBodyBDBPartitionHandler implements PartitionHandler {

	@Override
	public int partition(Database db, DatabaseEntry key) {
		return (int)key.getData()[3];
	}

}
