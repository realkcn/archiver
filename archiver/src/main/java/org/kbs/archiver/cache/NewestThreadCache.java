package org.kbs.archiver.cache;

import org.kbs.archiver.ThreadEntity;
import org.kbs.archiver.persistence.FrontPageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * User: kcn
 * Date: 12-3-21
 * Time: 上午10:56
 */
public class NewestThreadCache extends TimerTask {
    private static final Logger LOG = LoggerFactory.getLogger(NewestThreadCache.class);
    private boolean inited=false;

    public void setFrontPageMapper(FrontPageMapper frontPageMapper) {
        this.frontPageMapper = frontPageMapper;
    }

    private FrontPageMapper frontPageMapper;

    public int getOffsetdate() {
        return offsetdate;
    }

    public void setOffsetdate(int offsetdate) {
        this.offsetdate = offsetdate;
    }

    private int offsetdate=3;

    public void startup()
    {
        LOG.info("init newest thread cache start");
        frontPageMapper.deleteAll();
        frontPageMapper.insertNewestThread(getOffsetdate()*24);
        inited=true;
        LOG.info("init newest thread cache end");
    }

    public void addthread(ThreadEntity thread,String groupid,String boardname) {
        thread.setGroupid(groupid);
        thread.setBoardname(boardname);
        frontPageMapper.addThread(thread);
    }

    public void updatethread(ThreadEntity thread) {
        frontPageMapper.updateThread(thread);
    }

    @Override
    public void run() {
        LOG.info("start newest thread cache");
//        frontPageMapper.insertNewestThread(24);
        frontPageMapper.deleteOldThread(getOffsetdate()*24);
        LOG.info("end newest thread cache");
    }
}
