package org.kbs.archiver.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

@SuppressWarnings("serial")
public class PagerTag extends TagSupport {
  Pager pager;

	public final void setUrlprefix(String urlprefix) {
    pager.setUrlprefix(urlprefix.replace("\"","%22"));
	}

	public final void setUrlsuffix(String urlsuffix) {
    pager.setUrlsuffix(urlsuffix.replace("\"","%22"));
	}

	public final void setMaxIndexPages(int maxIndexPages) {
    pager.setMaxIndexPages(maxIndexPages);
	}

	// dirty setting
	private static String defaultid = "__kbs_pager__";

	public void setJsgoGenerate(boolean jsgoGenerate) {
    pager.setJsgoGenerate(jsgoGenerate);
	}

  @Override
  public void setPageContext(PageContext pageContext) {
    pager=new Pager();
    super.setPageContext(pageContext);
  }

  public static final String getDefaultid() {
		return defaultid;
	}

	public static final void setDefaultid(String id) {
		defaultid = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public final void setTotalpage(int totalpage) {
		pager.setTotalpage(totalpage);
	}

	public final void setUrl(String url) {
		pager.setUrl(url);
	}

	public final void setTotal(int total) {
		pager.setTotal(total);
	}

	public final void setPagesize(int pagesize) {
		pager.setPagesize(pagesize);
	}

	public final void setCurrentpage(int currentpage) {
	 pager.setCurrentpage(currentpage);
	}



	@Override
	public int doStartTag() throws JspException {
		pageContext.getRequest().setAttribute(defaultid, pager);
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		if (pager.getPagesize()==0) pager.setPagesize(20);
    pager.setTotalpage(pager.getTotal() / pager.getPagesize()
            + ((pager.getTotal() % pager.getPagesize()> 0) ? 1 : 0));
		pageContext.getRequest().setAttribute("currentPageNumber",new Integer(pager.getCurrentpage()));
		pageContext.getRequest().setAttribute("totalpage",new Integer(pager.getTotalpage()));
		if (pager.isJsgoGenerate()) {
			try {
				pageContext.getOut().append("<script type=\"text/javascript\">\nfunction _kbspagergo(a) {\n" +
						"window.location.href='"+pager.getUrlprefix()
						+"'+document.getElementById(a).value+'"
						+pager.getUrlsuffix()
						+"';\n}</script>"
						);
			} catch (IOException e) {
				throw new JspException(e);
			}
		}
		return EVAL_PAGE;
	}


}
