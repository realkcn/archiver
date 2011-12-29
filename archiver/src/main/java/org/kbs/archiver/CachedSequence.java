package org.kbs.archiver;

import org.kbs.archiver.persistence.SequenceMapper;
import org.kbs.library.SimpleException;

public class CachedSequence {
	private String name;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value.get();
	}

	public void setValue(long value) {
		this.value.set(value);
	}

	private java.util.concurrent.atomic.AtomicLong value=new java.util.concurrent.atomic.AtomicLong();

	private SequenceMapper seqMapper;
	
	public SequenceMapper getSeqMapper() {
		return seqMapper;
	}

	public void setSeqMapper(SequenceMapper seqMapper) {
		this.seqMapper = seqMapper;
	}

	public CachedSequence() {
		name = "";
		value.set(0);
	}
	
	public CachedSequence(String str) {
		name = str;
//		load();
	}
	public CachedSequence(String str,SequenceMapper seqMapper) {
		name = str;
		setSeqMapper(seqMapper);
		load();
	}
	public synchronized void  load() {
		/*
		PreparedStatement stmt=null;
		ResultSet rs=null;
		PreparedStatement stmt2=null;
		try {
			stmt = conn.prepareStatement("select value from sequence where name=?");
			stmt.setString(1, name);
			rs = stmt.executeQuery();
			if (rs.next()) {
				value.set(rs.getInt(1));
			} else {
				// new sequence
				stmt2 = conn
						.prepareStatement("insert into sequence(name,value) values(?,?)");
				stmt2.setString(1, name);
				stmt2.setInt(2, 0);
				value.set(0);
				stmt2.executeUpdate();
				stmt2.close();
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SimpleException("load sequence error:", e.getMessage());
		} finally {
			DBTools.closeQuietly(rs); 
			DBTools.closeQuietly(stmt); 
			DBTools.closeQuietly(stmt2); 
		}
		*/
		try {
			value.set(seqMapper.select(name));
		} catch (org.apache.ibatis.binding.BindingException e) {
			value.set(0);
		}
	}

	public long next() {
		return value.incrementAndGet();
	}

	public synchronized void flush() {
		/*
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("update sequence set values=?");
			stmt.setInt(1, value.get());
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SimpleException("update sequence error:", e.getMessage());
		}
		*/
		seqMapper.insert(new SequenceEntity(name, value.get()));
	}
}
