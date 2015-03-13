package org.kbs.archiver.tag;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class GoPageTag extends PagerSupport {
	private int newpage=-1;
	public final void setNewpage(int newpage) {
		this.newpage = newpage;
	}
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		if (newpage==-1) {
			newpage=(Integer)pageContext.getAttribute(PagesTag.PAGE_NUMBER);
		}
		return doJumpStartTag(newpage);
	}
}
