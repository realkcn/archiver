package org.kbs.archiver;

import java.io.IOException;
import java.util.Properties;

import org.kbs.library.*;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.FileNotFoundException;

public class SequenceDB {
    private static final Logger LOG = LoggerFactory.getLogger(SequenceDB.class);
    private static Database sequenceDb = null;
    
    public void init() throws SimpleException {
		Properties props=new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream("archiver.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new SimpleException("load properties error"+e.getMessage());
		}
        try {
        	DatabaseConfig envConfig = new DatabaseConfig();
            
            envConfig.setAllowCreate(true);
            envConfig.setTransactional(false); 
            
            envConfig.setErrorStream(System.err);
            envConfig.setType(DatabaseType.HASH);
            sequenceDb = new Database(props.getProperty("bdbdir")+"/sequence.db",
                    null,
                    envConfig);
        } catch (FileNotFoundException fnfe) {
        	throw new SimpleException("bdb file not found:"+props.getProperty("bdbdir")+"/sequence.db");
        } catch (DatabaseException e) {
        	throw new SimpleException("load bdb "+props.getProperty("bdbdir")+"/sequence.db"+"error",e.getMessage());
		}
	}
    
    public void put(String name,int value) {
        DatabaseEntry key =new DatabaseEntry(name.getBytes());
        DatabaseEntry data = new DatabaseEntry(new Integer(value).toString().getBytes());
        try {
			sequenceDb.put(null, key, data);
		} catch (DatabaseException e) {
			LOG.error("put error:",e);
		}
    }
    
    public int get(String name) {
    	int value=0;
        DatabaseEntry key =new DatabaseEntry(name.getBytes());
        DatabaseEntry data = new DatabaseEntry();
        try {
			if (sequenceDb.get(null, key, data, null)==OperationStatus.NOTFOUND) {
				put(name,0);
				return 0;
			}
			value=Integer.valueOf(new String(data.getData())).intValue();
		} catch (DatabaseException e) {
			LOG.error("get error:",e);
		}
        return value;
    }
    public void shutdown() {
		try {
			sequenceDb.close();
		} catch (DatabaseException e) {
			LOG.error("shutdown error:",e);
		}
    }
}
