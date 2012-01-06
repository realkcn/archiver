package org.kbs.archiver.tag;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class PrevPageTag extends PagerSupport {
	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		int newpage=getPager().getCurrentpage()-1;
		return doJumpStartTag(newpage);
	}

	@Override
	public int doEndTag() throws JspException {
		return super.doEndTag();
	}

}
