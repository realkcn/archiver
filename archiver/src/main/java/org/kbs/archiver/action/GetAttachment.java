package org.kbs.archiver.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.kbs.archiver.AttachmentEntity;
import org.kbs.archiver.persistence.AttachmentMapper;

import com.opensymphony.xwork2.ActionSupport;
import javax.activation.MimetypesFileTypeMap;

public class GetAttachment extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1484991006651780114L;
	private AttachmentEntity attachment;
	private AttachmentMapper attachmentMapper;
	String encodingURL;
	String contentType;

	public AttachmentEntity getAttachment() {
		return attachment;
	}

	public void setAttachment(AttachmentEntity attachment) {
		this.attachment = attachment;
	}

	public AttachmentMapper getAttachmentMapper() {
		return attachmentMapper;
	}

	public void setAttachmentMapper(AttachmentMapper attachmentMapper) {
		this.attachmentMapper = attachmentMapper;
	}

	public String getEncodingURL() {
		return encodingURL;
	}

	public void setEncodingURL(String encodingURL) {
		this.encodingURL = encodingURL;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream getInputStream() throws Exception {
		InputStream is=new ByteArrayInputStream(attachment.getData());
//		System.out.println("-----"+(is==null));
		return is;
	}

	public String get() throws Exception {
		attachment = attachmentMapper.getByEncodingUrl(encodingURL);
//		System.out.println("---"+encodingURL);
		if (attachment == null) {
			this.addActionError("附件找不到");
			return ERROR;
		}
		contentType = new MimetypesFileTypeMap().getContentType(attachment
				.getName().toLowerCase());// 保存文件的类型
//		System.out.println("---"+attachment.getName()+"--"+contentType);
		return SUCCESS;
	}
}
