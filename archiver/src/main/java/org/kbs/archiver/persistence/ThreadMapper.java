package org.kbs.archiver.persistence;

import java.util.List;

import org.kbs.archiver.ThreadEntity;

public interface ThreadMapper {
	public ThreadEntity get(long threadid);
	public ThreadEntity getByOriginId(long orginid);
	public void insert(ThreadEntity thread);
	public int update(ThreadEntity thread);
	public List<ThreadEntity> getThreadsOnBoard(long boardid);
}
