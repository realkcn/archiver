package org.kbs.archiver;

import java.sql.*;


import org.kbs.library.*;

public class Board {

	private int boardid;
	private String name;
	private int threads;
	private int articles;
	private boolean ishidden;
	public int getBoardid() {
		return boardid;
	}
	public void setBoardid(int boardid) {
		this.boardid = boardid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getThreads() {
		return threads;
	}
	public void setThreads(int threads) {
		this.threads = threads;
	}
	public int getArticles() {
		return articles;
	}
	public void setArticles(int articles) {
		this.articles = articles;
	}
	
	public boolean isIshidden() {
		return ishidden;
	}
	public void setIshidden(boolean ishidden) {
		this.ishidden = ishidden;
	}
	public void flushtoDB() {
		Connection conn=null;
		PreparedStatement stmt=null;
		try {
			conn=DBTools.getConnection();
			stmt = conn.prepareStatement("update board set boardid=?,threads=?,articles=?,ishidden=? where name=?");
			stmt.setInt(1, boardid);
			stmt.setInt(2, threads);
			stmt.setInt(3, articles);
			stmt.setBoolean(4, ishidden);
			stmt.setString(5, name);
			if (stmt.executeUpdate()==0) {
				DBTools.closeQuietly(stmt);
				stmt = conn.prepareStatement("insert into board(boardid,threads,articles,ishidden,name) values(?,?,?,?,?)");
				stmt.setInt(1, boardid);
				stmt.setInt(2, threads);
				stmt.setInt(3, articles);
				stmt.setBoolean(4, ishidden);
				stmt.setString(5, name);
				stmt.executeUpdate();
			}
		} catch (SimpleException e) {
			GlobalLogger.getLogger().error("Load Board from DB:",e);
		} catch (SQLException e) {
			GlobalLogger.getLogger().error("Load Board from DB:",e);
		}
		finally {
			DBTools.closeQuietly(stmt);
			DBTools.closeQuietly(conn);
		}
	}

	public void loadBoardFromDB(String name) {
		Connection conn=null;
		try {
			conn=DBTools.getConnection();
			PreparedStatement stmt = conn.prepareStatement("select boardid,threads,articles,ishidden from board where name=?");
			stmt.setString(1, name);
			ResultSet rs=stmt.executeQuery();
			if (rs.next()) {
				this.boardid=rs.getInt(1);
				this.name=name;
				this.threads=rs.getInt(2);
				this.articles=rs.getInt(3);
				this.ishidden=rs.getBoolean(4);
			} else {
				throw new SimpleException("No such board:"+name);
			}
		} catch (SimpleException e) {
			GlobalLogger.getLogger().error("Load Board from DB:",e);
		} catch (SQLException e) {
			GlobalLogger.getLogger().error("Load Board from DB:",e);
		}
		finally {
			DBTools.closeQuietly(conn);
		}
	}
}
