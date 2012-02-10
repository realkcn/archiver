package org.kbs.archiver.persistence;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.kbs.archiver.ArticleEntity;

public interface ArticleMapper {
		public ArticleEntity get(long articleid);
		public ArticleEntity getByOriginId(@Param("boardid")long boardid,@Param("originid")long originid);
		public ArticleEntity getByEncodingUrl(String encodingurl);
		public void deleteByBoard(long boardid);
		public void insert(ArticleEntity article);
		public int update(ArticleEntity article);
		public List<ArticleEntity> getArticlesOnThread(long threadid);
		public List<ArticleEntity> getByThreadPerPage(@Param("threadid")long threadid,@Param("offset") int offset, @Param("limit") int limit);
		public List<ArticleEntity> getByAuthorPerPage(@Param("author")String author,@Param("offset") int offset, @Param("limit") int limit);
		public int countByAuthor(@Param("author")String author);
		public void delete(long articleid);
		public List<String> getFilenamesByBoard(long boardid);
}
