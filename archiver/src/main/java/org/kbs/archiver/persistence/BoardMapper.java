package org.kbs.archiver.persistence;

import java.util.List;

import org.kbs.archiver.BoardEntity;

public interface BoardMapper {
	public BoardEntity get(int boardid);
	public BoardEntity getByName(String name);
	public void deleteByName(String name);
	public void insert(BoardEntity board);
	public void update(BoardEntity board);
	public void updateByName(BoardEntity board);
	public void updateLast(BoardEntity board);
	public List<BoardEntity> selectAll();
}
