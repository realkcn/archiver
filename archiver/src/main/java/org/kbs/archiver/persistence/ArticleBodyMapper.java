package org.kbs.archiver.persistence;

import org.apache.ibatis.annotations.Param;

public interface ArticleBodyMapper {
	String get(long articleid);
	void put(@Param("articleid")long articleid,@Param("body")String body);
	void delete(long articleid);
}
