package org.kbs.archiver.tag;

import javax.servlet.jsp.JspException;

@SuppressWarnings("serial")
public class JSGoTag extends PagerSupport {
	private String elementid="pager.gopage";

	public final String getElementid() {
		return elementid;
	}

	public final void setElementid(String elementid) {
		this.elementid = elementid;
	}

	@Override
	public int doStartTag() throws JspException {
		super.doStartTag();
		if (!getPager().isJsgoGenerated()) {
			
		}
		pageContext.getOut().append("javascript:{windows.location.href=document"+);
		return SKIP_BODY;
	}
}
