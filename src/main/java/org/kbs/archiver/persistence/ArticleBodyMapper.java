package org.kbs.archiver.persistence;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface ArticleBodyMapper {
	String get(long articleid);
	void put(@Param("articleid")long articleid,@Param("body")String body);
	void add(@Param("articleid")long articleid,@Param("body")String body);
	void addMap(Map<String,Object> map);
	void delete(long articleid);
}
