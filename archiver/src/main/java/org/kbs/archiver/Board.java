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
	public void loadBoardFromDB(String name) {
		Connection conn=null;
		try {
			conn=DBTools.getConnection();
			PreparedStatement stmt = conn.prepareStatement("select boardid,threads,articles,hidden from board where name=?");
			stmt.setString(1, name);
			ResultSet rs=stmt.executeQuery();
			this.boardid=rs.getInt(1);
			this.name=name;
			this.threads=rs.getInt(2);
			this.articles=rs.getInt(3);
			this.ishidden=rs.getBoolean(4);
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
