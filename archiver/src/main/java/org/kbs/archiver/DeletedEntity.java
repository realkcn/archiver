package org.kbs.archiver;/**
 * User: kcn
 * Date: 12-9-26
 * Time: 下午4:53
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class DeletedEntity {
    private static final Logger LOG = LoggerFactory.getLogger(DeletedEntity.class);

    private long boardid;
    private long originid;
    private Date deletetime;
    private String deleteby;
    private String cname;
    private String author;
    private long articleid;
    private Date posttime;
    private String subject;
    private  String encodingurl;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getArticleid() {
        return articleid;
    }

    public void setArticleid(long articleid) {
        this.articleid = articleid;
    }

    public Date getPosttime() {
        return posttime;
    }

    public void setPosttime(Date posttime) {
        this.posttime = posttime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEncodingurl() {
        return encodingurl;
    }

    public void setEncodingurl(String encodingurl) {
        this.encodingurl = encodingurl;
    }

    @Override
    public String toString() {
        return "DeletedEntity{" +
                "boardid=" + boardid +
                ", originid=" + originid +
                ", deletetime=" + deletetime +
                ", deleteby='" + deleteby + '\'' +
                '}';
    }

    public long getBoardid() {
        return boardid;
    }

    public void setBoardid(long boardid) {
        this.boardid = boardid;
    }

    public long getOriginid() {
        return originid;
    }

    public void setOriginid(long originid) {
        this.originid = originid;
    }

    public Date getDeletetime() {
        return deletetime;
    }

    public void setDeletetime(Date deletetime) {
        this.deletetime = deletetime;
    }

    public String getDeleteby() {
        return deleteby;
    }

    public void setDeleteby(String deleteby) {
        this.deleteby = deleteby;
    }
}
