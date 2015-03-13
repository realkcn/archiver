package org.kbs.archiver.tag;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class LastPageTag extends PagerSupport {
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		int newpage = getPager().getTotalpage();
		return doJumpStartTag(newpage);
	}
}
