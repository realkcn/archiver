package org.kbs.archiver.persistence;

import org.kbs.archiver.Board;

public interface BoardMapper {
	public Board getBoardByid(int boardid);
	public Board getBoardByName(String name);
	public void update(Board board);
}
