package org.kbs.archiver.action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.lang.StringUtils;
import org.kbs.archiver.persistence.ThreadMapper;

/**
 * User: kcn
 * Date: 12-3-29
 * Time: 下午11:55
 */
public class SearchThread extends ActionSupport{
    /**
     * atrticle encoding url
     */
    private String aurl;
    private String threadurl;

    public String getThreadurl() {
        return threadurl;
    }

    public String getAurl() {
        return aurl;
    }

    public void setAurl(String aurl) {
        this.aurl = aurl;
    }

    public void setThreadurl(String threadurl) {
        this.threadurl = threadurl;
    }

    private ThreadMapper threadMapper;

    public ThreadMapper getThreadMapper() {
        return threadMapper;
    }

    public void setThreadMapper(ThreadMapper threadMapper) {
        this.threadMapper = threadMapper;
    }

    public String searchByArticleURL() throws Exception {
        if (!StringUtils.isEmpty(aurl)) {
            threadurl=threadMapper.getByArticleURL(aurl);
            if (!StringUtils.isEmpty(threadurl))
                return SUCCESS;
        }
        return ERROR;
    }
}
