package org.kbs.archiver.action.admin;

import org.apache.struts2.ServletActionContext;
import org.kbs.archiver.*;
import org.kbs.archiver.persistence.*;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
public class Delete extends ActionSupport {

	private String articleid = null;
	private String threadid = null;
	private String author = null;
	private String boardname = null;
	private BoardMapper boardMapper;
	private ThreadMapper threadMapper;
	private ArticleMapper articleMapper;
	private String originid=null;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getThreadid() {
		return threadid;
	}

	public void setThreadid(String threadid) {
		this.threadid = threadid;
	}

	public BoardMapper getBoardMapper() {
		return boardMapper;
	}

	public void setBoardMapper(BoardMapper boardMapper) {
		this.boardMapper = boardMapper;
	}

	public ThreadMapper getThreadMapper() {
		return threadMapper;
	}

	public void setThreadMapper(ThreadMapper threadMapper) {
		this.threadMapper = threadMapper;
	}

	public ArticleMapper getArticleMapper() {
		return articleMapper;
	}

	public void setArticleMapper(ArticleMapper articleMapper) {
		this.articleMapper = articleMapper;
	}

	public String getArticleid() {
		return articleid;
	}

	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}

	/**
	 * @return the boardname
	 */
	public String getBoardname() {
		return boardname;
	}

	/**
	 * @param boardname
	 *            the boardname to set
	 */
	public void setBoardname(String boardname) {
		this.boardname = boardname;
	}

	/**
	 * @return the originid
	 */
	public String getOriginid() {
		return originid;
	}

	/**
	 * @param originid the originid to set
	 */
	public void setOriginid(String originid) {
		this.originid = originid;
	}

	public String deleteByAuthor() throws Exception {
		if (author != null) {
			long count = articleMapper.countByAuthor(author);
			if (count > 0) {
				WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getRequiredWebApplicationContext(ServletActionContext
								.getServletContext());
				ArchiverService service = new ArchiverService(
						webApplicationContext);
				service.deleteByAuthor(author);
				this.addActionMessage("同作者删除成功。author=" + author);
				return SUCCESS;
			} else
				addActionError("找不到 " + author + " 发的文章");
		} else
			addActionError("请输入作者id");
		return ERROR;
	}

	public String deleteThread() throws Exception {
		if (threadid != null) {
			ThreadEntity thread = threadMapper.getByEncodingUrl(threadid);
			if (thread != null) {
				WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getRequiredWebApplicationContext(ServletActionContext
								.getServletContext());
				ArchiverService service = new ArchiverService(
						webApplicationContext);
				service.deleteThread(thread.getThreadid());
				this.addActionMessage("删除主题成功。id=" + threadid);
				return SUCCESS;
			} else
				addActionError("找不到id为" + threadid + "的主题");
		} else
			addActionError("请输入主题id");
		return ERROR;
	}

	public String deleteArticle() throws Exception {
		if (articleid != null) {
			ArticleEntity article = articleMapper.getByEncodingUrl(articleid);
			if (article != null) {
				WebApplicationContext webApplicationContext = WebApplicationContextUtils
						.getRequiredWebApplicationContext(ServletActionContext
								.getServletContext());
				ArchiverService service = new ArchiverService(
						webApplicationContext);
				service.deleteArticle(article.getArticleid());
				this.addActionMessage("删除文章成功。id=" + articleid);
				return SUCCESS;
			} else
				addActionError("没有找到id为" + article + "的文章");
		} else
			addActionError("请输入文章id");
		return ERROR;
	}

	public String SearchByOriginId() throws Exception {
		ArticleEntity article=GetArticleByOriginId();
		if (article!=null) {
			ActionContext ctx=ActionContext.getContext();
			articleid=article.getEncodingurl();
			ctx.put("showurl", "article");
			return SUCCESS;
		}
		return ERROR;
	}
	
	private ArticleEntity GetArticleByOriginId() throws Exception {
		if (boardname != null) {
			if (originid != null) {
				BoardEntity board = boardMapper.getByName(boardname);
				if (board != null) {
					try {
						long id = Long.parseLong(originid);
						ArticleEntity article = articleMapper.getByOriginId(
								board.getBoardid(), id);
						if (article != null) {
							return article;
						} else
							addActionError("没有找到id为" + originid + "的文章");
					} catch (NumberFormatException ex) {
						addActionError("错误的id格式：" + originid + "。应为十进制数");
					}
				} else
					addActionError("没有找到名为" + boardname + "的版面。请注意区分大小写");
			} else
				addActionError("请输入文章id");
		} else
			addActionError("请输入版面名");
		return null;
	}
	
	public String DeleteArticleByOriginId() throws Exception {
		ArticleEntity article=GetArticleByOriginId();
		if (article!=null) {
			WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getRequiredWebApplicationContext(ServletActionContext
							.getServletContext());
			ArchiverService service = new ArchiverService(
					webApplicationContext);
			service.deleteArticle(article.getArticleid());
			this.addActionMessage("删除文章成功。版面名："+boardname+" id=" + originid);
			return SUCCESS;
		}
		return ERROR;
	}
}
