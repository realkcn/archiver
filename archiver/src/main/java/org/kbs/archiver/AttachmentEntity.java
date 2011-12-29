package org.kbs.archiver;

public class AttachmentEntity {
	private long attachmentid;
	private long articleid;
	private String name;
	private int order;
	private String encodingurl;
	private byte [] data;
	private long boardid;
	public long getBoardid() {
		return boardid;
	}
	public void setBoardid(long boardid) {
		this.boardid = boardid;
	}
	public long getAttachmentid() {
		return attachmentid;
	}
	public void setAttachmentid(long attachmentid) {
		this.attachmentid = attachmentid;
	}
	public long getArticleid() {
		return articleid;
	}
	public void setArticleid(long articleid) {
		this.articleid = articleid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public String getEncodingurl() {
		return encodingurl;
	}
	public void setEncodingurl(String encodingurl) {
		this.encodingurl = encodingurl;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
}
