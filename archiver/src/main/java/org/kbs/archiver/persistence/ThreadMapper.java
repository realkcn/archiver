package org.kbs.archiver.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.ThreadEntity;

public interface ThreadMapper {
	public ThreadEntity get(long threadid);
	public ThreadEntity getByOriginId(@Param("boardid")long boardid,@Param("originid")long originid);
	public ThreadEntity getByEncodingUrl(String encodingurl);

	public void insert(ThreadEntity thread);
	public int update(ThreadEntity thread);
	public void deleteByBoard(long boardid);
	public List<ThreadEntity> getThreadsOnBoard(long boardid);
	List<ThreadEntity> getByBoardPerPage(@Param("boardid")long boardid,@Param("offset") int offset, @Param("limit") int limit);  //这里修改了，使用了mybatis 3.x提供的注解的方法
	public void delete(long threadid);
}
