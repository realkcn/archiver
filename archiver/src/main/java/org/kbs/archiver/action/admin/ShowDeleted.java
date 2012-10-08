package org.kbs.archiver.action.admin;/**
 * User: kcn
 * Date: 12-10-8
 * Time: 下午4:46
 */

import com.opensymphony.xwork2.ActionSupport;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.DeletedEntity;
import org.kbs.archiver.persistence.DeletedMapper;
import org.kbs.archiver.util.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ShowDeleted extends ActionSupport {
    private static final Logger LOG = LoggerFactory.getLogger(ShowDeleted.class);

    private List<DeletedEntity> articlelist;

    @Autowired
    private DeletedMapper deletedMapper;

    public DeletedMapper getDeletedMapper() {
        return deletedMapper;
    }

    public void setDeletedMapper(DeletedMapper deletedMapper) {
        this.deletedMapper = deletedMapper;
    }

    private Pager pager;

    public int getPagesize() {
        return pager.getPagesize();
    }

    public int getPageno() {
        return pager.getPageno();
    }

    private int inputPageno;
    public void setPageno(int no) {
        this.inputPageno=no;
    }
    public int getTotalpage() {
        return pager.getTotalpage();
    }

    public List<DeletedEntity> getArticlelist() {
        return articlelist;
    }

    public void setArticlelist(List<DeletedEntity> articlelist) {
        this.articlelist = articlelist;
    }

    // action for get articles
    public String show() throws Exception {
        int totalsize=deletedMapper.count();
        pager=new Pager(inputPageno, 0, totalsize);
        articlelist = deletedMapper.getArticles(pager.getStart(), pager.getPagesize());
        return SUCCESS;
    }
}
