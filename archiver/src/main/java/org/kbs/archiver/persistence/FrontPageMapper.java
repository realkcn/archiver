package org.kbs.archiver.persistence;

import org.apache.ibatis.annotations.Param;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.ThreadEntity;

import java.util.List;

/**
 * User: kcn
 * Date: 12-3-21
 * Time: 上午10:31
 */
public interface FrontPageMapper {
    public List<ThreadEntity> getHotThreads(@Param("groupid")String groupid,@Param("count")int count);
    public List<ThreadEntity> getNewestThreads(@Param("count")int count);
    public List<String> getGroups();
    public void insertNewestThread(@Param("offsethour")int offsethour);
    public void deleteOldThread(@Param("offsethour")int offsethour);
    public void deleteAll();
}
