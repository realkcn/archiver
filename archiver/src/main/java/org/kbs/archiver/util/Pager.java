package org.kbs.archiver.util;

public class Pager {
	private int totalsize;
	private int totalpage;
	private int pageno;
	private int pagesize;
	
	public Pager(int pageno,int pagesize,int totalsize) {
		if (pagesize == 0)
			pagesize = 20;
		if (pageno == 0)
			pageno = 1;
		this.pageno=pageno;
		this.pagesize=pagesize;
		this.totalsize=totalsize;
		totalpage = totalsize/ pagesize	+ ((totalsize % pagesize > 0) ? 1 : 0);
		if ((pageno - 1) * pagesize > totalsize) {
			pageno = totalsize / pagesize + 1;
		} else if ((pageno - 1) * pagesize == totalsize) {
			pageno = totalsize / pagesize;
		}
	}

	public int getStart() {
		return (pageno-1)*pagesize;
	}
	public int getEnd() {
		if (pageno*pagesize>totalsize)
			return totalsize;
		return pageno*pagesize;
	}
	public int getTotalsize() {
		return totalsize;
	}

	public int getTotalpage() {
		return totalpage;
	}

	public int getPageno() {
		return pageno;
	}

	public int getPagesize() {
		return pagesize;
	}
}
