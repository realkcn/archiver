package org.kbs.archiver.persistence;

public interface ArticleBodyMapper {
	String get(long articleid);
	void put(long articleid,String body);
	void delete(long articleid);
}
