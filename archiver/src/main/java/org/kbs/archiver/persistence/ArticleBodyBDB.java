package org.kbs.archiver.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;

import org.kbs.library.Converter;
import org.kbs.library.SimpleException;

import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArticleBodyBDB implements ArticleBodyMapper {
    private static Database bodyDb = null;
    private static final Logger LOG = LoggerFactory.getLogger(ArticleBodyBDB.class);
    
    @Override
	public void add(long articleid, String body) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMap(Map<String, Object> map) {
		// TODO Auto-generated method stub
		
	}

	public void init() throws SimpleException {
		Properties props=new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream("archiver.properties"));
		} catch (IOException e) {
			throw new SimpleException("load properties error"+e.getMessage());
		}
        try {
        	DatabaseConfig envConfig = new DatabaseConfig();
            
            envConfig.setAllowCreate(true);
            envConfig.setTransactional(false); 
            
            envConfig.setErrorStream(System.err);
            envConfig.setType(DatabaseType.HASH);
            /*
            envConfig.setPartitionByCallback(16,new ArticleBodyBDBPartitionHandler());
            File[] partitionDirs=new File[16];
            for (int i=0;i<16;i++) {
            	partitionDirs[i]=new File(props.getProperty("bdbdir")+"/artilebody"+i+".db");
            }
            envConfig.setPartitionDirs(partitionDirs);
            */
            bodyDb = new Database(props.getProperty("bdbdir")+"/articlebody.db",
                    "articlebody",
                    envConfig);
        } catch (FileNotFoundException fnfe) {
        	throw new SimpleException("bdb file not found:"+props.getProperty("bdbdir")+"/articlebody.db");
        } catch (DatabaseException e) {
        	throw new SimpleException("load bdb "+props.getProperty("bdbdir")+"/articlebody.db"+"error",e.getMessage());
		}
    }
    
    @Override
    public void put(long articleid,String value) {
        DatabaseEntry key =new DatabaseEntry(Converter.longToByteArray(articleid));
        DatabaseEntry data = new DatabaseEntry(value.getBytes());
        try {
			bodyDb.put(null, key, data);
		} catch (DatabaseException e) {
			LOG.error("put "+articleid+" error:",e);
		}
    }
    
    
    @Override
	public void delete(long articleid) {
        DatabaseEntry key =new DatabaseEntry(Converter.longToByteArray(articleid));
        try {
			bodyDb.delete(null, key);
		} catch (DatabaseException e) {
			LOG.error("delete "+articleid+" error:",e);
		}
	}

	@Override
    public String get(long articleid) {
    	String value=null;
        DatabaseEntry key =new DatabaseEntry(Converter.longToByteArray(articleid));
        DatabaseEntry data = new DatabaseEntry();
        try {
			if (!(bodyDb.get(null, key, data, null)==OperationStatus.NOTFOUND)) {
				value=new String(data.getData(),"UTF-8");
			}
		} catch (DatabaseException e) {
			LOG.error("get "+articleid+" error:",e);
		} catch (UnsupportedEncodingException e) {
			LOG.error("get "+articleid+" error:",e);
		}
        return value;
    }
    public void shutdown() {
		try {
			bodyDb.close();
		} catch (DatabaseException e) {
			LOG.error("shutdown:",e);
		}
    }
}
