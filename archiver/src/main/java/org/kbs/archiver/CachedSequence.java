package org.kbs.archiver;

import org.kbs.library.SimpleException;

public class CachedSequence {
	String name;
	java.util.concurrent.atomic.AtomicInteger value;

	CachedSequence() {
		name = "";
		value.set(0);
	}
	
	CachedSequence(String str) {
		name = str;
//		load();
	}

	void load(SequenceDB db) throws SimpleException {
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
		value.set(db.get(name));
	}

	int next() {
		return value.incrementAndGet();
	}

	void flush(SequenceDB db) throws SimpleException {
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
		db.put(name, value.get());
	}
}
