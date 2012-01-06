package org.kbs.archiver.tag;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class FirstPageTag extends PagerSupport {
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		int newpage = 1;
		return doJumpStartTag(newpage);
	}
}
