package org.kbs.archiver.persistence;

import java.util.List;

import org.kbs.archiver.Board;

public interface BoardMapper {
	public Board getByid(int boardid);
	public Board getByName(String name);
	public void deleteByName(String name);
	public void insert(Board board);
	public void update(Board board);
	public void updateByName(Board board);
	public List<Board> selectAll();
}
