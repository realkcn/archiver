package org.kbs.archiver.action.user;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.kbs.archiver.ArchiverService;
import org.kbs.archiver.ArticleEntity;
import org.kbs.archiver.persistence.ArticleMapper;
import org.kbs.library.spring.ApplicationContextInstance;
import org.kbs.sso.client.SSOFilter;
import org.kbs.sso.principal.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: kcn
 * Date: 12-4-24
 * Time: 下午4:34
 */
public class Delete extends ActionSupport implements ServletRequestAware {
    private static final Logger LOG = LoggerFactory
            .getLogger(Delete.class);

    protected String encodingURL;

    @Autowired
    protected ArticleMapper articleMapper;

    public String getEncodingURL() {
        return encodingURL;
    }

    public void setEncodingURL(String encodingURL) {
        this.encodingURL = encodingURL;
    }

    HttpServletRequest request;
    public String deleteArticle() {
        AttributePrincipal principal= SSOFilter.getPrincipal(request);
        if (principal==null) {
            addActionError("请登录");
            return "redirect";
        }
        if (!StringUtils.isEmpty(encodingURL)) {
            ArticleEntity article=articleMapper.getByEncodingUrl(encodingURL);
            if (article==null) {
                addActionError("找不到这篇帖子");
                return ERROR;
            }
            if ((article.getAuthor().toLowerCase().equals(principal.getName().toLowerCase()))
                    &&(article.getPosttime().getTime()>=Long.valueOf((String) principal.get("firstlogin")))) {
                ArchiverService service = new ArchiverService(
                        ApplicationContextInstance.getApplicationContext());
                service.invisibleArticle(article.getArticleid());
                addActionMessage("删除成功");
            } else {
                addActionError("这不是你发的帖子");
                return ERROR;
            }
        }
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request=httpServletRequest;
    }
}
