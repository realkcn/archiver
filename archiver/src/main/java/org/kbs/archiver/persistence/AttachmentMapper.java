package org.kbs.archiver.persistence;

import java.util.List;

import org.kbs.archiver.AttachmentEntity;
public interface AttachmentMapper {
	public AttachmentEntity get(long attachmentid);
	public void delete(long attachmentid);
	public void deleteByBoard(long boardid);
	public void insert(AttachmentEntity board);
	public void update(AttachmentEntity board);
	public List<AttachmentEntity> selectAll();
	public List<AttachmentEntity> getByArticle(long articleid);
}
