package org.kbs.archiver.tag;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class NextPageTag extends PagerSupport {
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		int newpage=getPager().getCurrentpage()+1;
		return doJumpStartTag(newpage);
	}
}
