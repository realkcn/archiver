package org.kbs.archiver.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

@SuppressWarnings("serial")
public class PagerTag extends TagSupport {

	// Tag Properties
	private String url = null;
	// private String id;
	private int total;
	private int pagesize;
	private int currentpage;
	private int maxIndexPages=10;
	private String urlprefix=null;
	private String urlsuffix=null;
	private boolean jsgoGenerate=true;

	public final String getUrlprefix() {
		return urlprefix;
	}

	public final void setUrlprefix(String urlprefix) {
		this.urlprefix = urlprefix.replace("\"","%22");
	}

	public final String getUrlsuffix() {
		return urlsuffix;
	}

	public final void setUrlsuffix(String urlsuffix) {
		this.urlsuffix = urlsuffix.replace("\"","%22");
	}

	public final int getMaxIndexPages() {
		return maxIndexPages;
	}

	public final void setMaxIndexPages(int maxIndexPages) {
		this.maxIndexPages = maxIndexPages;
	}

	// dirty setting
	private static String defaultid = "__kbs_pager__";

	private int totalpage;

	public boolean getJsgoGenerate() {
		return jsgoGenerate;
	}

	public void setJsgoGenerate(boolean jsgoGenerate) {
		this.jsgoGenerate = jsgoGenerate;
	}

	public static final String getDefaultid() {
		return defaultid;
	}

	public static final void setDefaultid(String id) {
		defaultid = id;
	}

	public boolean isValidPage(int pagenumber) {
		return (pagenumber > 0) && (pagenumber <= totalpage);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public final int getTotalpage() {
		return totalpage;
	}

	public final void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}

	public final String getUrl() {
		return url;
	}

	public final int getTotal() {
		return total;
	}

	public final int getPagesize() {
		return pagesize;
	}

	public final int getCurrentpage() {
		return currentpage;
	}

	public final void setUrl(String url) {
		this.url = url;
	}

	public final void setTotal(int total) {
		this.total = total;
	}

	public final void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public final void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	final int getFirstIndexPage() {
		int firstPage;
		int halfIndexPages = maxIndexPages / 2;

		// put the current page in middle of the index
		firstPage = Math.max(1, currentpage - halfIndexPages);
		return firstPage;
	}

	final int getLastIndexPage(int firstPage) {
		int halfIndexPages = maxIndexPages / 2;
		int lastpage;
		if (currentpage < halfIndexPages) {
			lastpage=maxIndexPages;
		} else {
			lastpage = firstPage + maxIndexPages;
		}
		if (lastpage>totalpage)
			lastpage=totalpage;
		return lastpage;
	}

	@Override
	public int doStartTag() throws JspException {
		pageContext.getRequest().setAttribute(defaultid, this);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		if (pagesize==0) pagesize=20;
		totalpage = total / pagesize + ((total % pagesize > 0) ? 1 : 0);
		pageContext.getRequest().setAttribute("currentPageNumber",new Integer(currentpage));
		pageContext.getRequest().setAttribute("totalpage",new Integer(totalpage));
		if (jsgoGenerate) {
			try {
				pageContext.getOut().append("<script type=\"text/javascript\">\nfunction _kbspagergo(a) {\n" +
						"window.location.href='"+urlprefix
						+"'+document.getElementById(a).value+'"
						+urlsuffix
						+"';\n}</script>"
						);
			} catch (IOException e) {
				throw new JspException(e);
			}
		}
		return EVAL_PAGE;
	}

	public String generateURL(int newpage) {
		if (url!=null)
			return String.format(url, newpage);
		else
			return urlprefix+newpage+urlsuffix;
	}

}
