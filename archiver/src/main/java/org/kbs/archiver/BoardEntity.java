package org.kbs.archiver;

import org.kbs.library.BoardHeaderInfo;

public class BoardEntity {

	private long boardid;
	private String name;
	private int threads;
	private int articles;
	private boolean ishidden;
	private long lastarticleid;
	private String cname;
	private String groupid;
	private String section;
	private boolean ignored;
	private boolean regenerate;
	
	@Override
	public String toString() {
		return "boardid:"+boardid+" name:"+name+" threads:"+threads+" articles:"+articles+" ishidden:"+ishidden+" lastarticleid:"+lastarticleid+" cname:"+cname+" ignore:"+ignored;
	}
	public BoardEntity() {
		
	}
	public BoardEntity(BoardHeaderInfo bh) {
		boardid=0;
		threads=0;
		articles=0;
		lastarticleid=0;
		set(bh);
	}
	public void set(BoardHeaderInfo bh) {
		name=bh.getFilename();
//		boardid=0;
//		threads=0;
//		articles=0;
		ishidden=!bh.isNormalBoard();
//		lastarticleid=0;
		cname=bh.getTitle();
		groupid=bh.getGroupid();
		section=bh.getSection();
	}
	public void setBoardid(long boardid) {
		this.boardid = boardid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public long getLastarticleid() {
		return lastarticleid;
	}
	public void setLastarticleid(long lastarticleid) {
		this.lastarticleid = lastarticleid;
	}
	public long getBoardid() {
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
	/*
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
	*/
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public boolean isIgnored() {
		return ignored;
	}
	public void setIgnored(boolean ignored) {
		this.ignored = ignored;
	}
	public boolean isRegenerate() {
		return regenerate;
	}
	public void setRegenerate(boolean regenerate) {
		this.regenerate = regenerate;
	}
}
