package org.kbs.archiver.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTag;

@SuppressWarnings("serial")
public class PagesTag extends PagerSupport implements BodyTag {

	private BodyContent bodyContent = null;
	private int page = 0;
	private int lastPage = 0;
	public static final String PAGE_NUMBER = "pageNumber";

	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();

		int firstPage = getPager().getFirstIndexPage();
		lastPage = getPager().getLastIndexPage(firstPage);
		page = firstPage;
		return (page <= lastPage ? EVAL_BODY_BUFFERED : SKIP_BODY);
	}

	@Override
	public void doInitBody() throws JspException {
		pageContext.setAttribute(PAGE_NUMBER, new Integer(page));
		page++;
	}

	@Override
	public int doAfterBody() throws JspException {
		if (page <= lastPage) {
			pageContext.setAttribute(PAGE_NUMBER, page);
			page++;
			return EVAL_BODY_AGAIN;
		} else {
			try {
				bodyContent.writeOut(bodyContent.getEnclosingWriter());
				return SKIP_BODY;
			} catch (IOException e) {
				throw new JspTagException(e.toString());
			}
		}
	}

	@Override
	public int doEndTag() throws JspException {
		bodyContent = null;
		super.doEndTag();
		pageContext.removeAttribute(PAGE_NUMBER);
		return EVAL_PAGE;
	}

	@Override
	public void release() {
		bodyContent = null;
		super.release();
	}

	@Override
	public void setBodyContent(BodyContent bc) {
		bodyContent = bc;
	}

}
