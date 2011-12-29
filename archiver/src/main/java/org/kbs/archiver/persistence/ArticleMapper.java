package org.kbs.archiver.persistence;
import java.util.List;

import org.kbs.archiver.ArticleEntity;

public interface ArticleMapper {
		public ArticleEntity get(long articleid);
		public ArticleEntity getByOriginId(long orginid);
		public void deleteByBoard(long boardid);
		public void insert(ArticleEntity article);
		public int update(ArticleEntity article);
		public List<ArticleEntity> getArticlesOnThread(long threadid);
}
