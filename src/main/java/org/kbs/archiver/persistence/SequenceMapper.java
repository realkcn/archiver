package org.kbs.archiver.persistence;

import org.kbs.archiver.SequenceEntity;

public interface SequenceMapper {
	public long select(String name);
	public void insert(SequenceEntity seq);
	public void update(SequenceEntity seq);
}
