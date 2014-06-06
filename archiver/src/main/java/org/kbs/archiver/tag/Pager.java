package org.kbs.archiver.tag;

/**
 * Created by kcn on 14-6-6.
 */
public class Pager {
  // Tag Properties
  private String url = null;
  // private String id;
  private int total;
  private int pagesize=0;
  private int currentpage=0;
  private int maxIndexPages=10;

  private String urlprefix=null;
  private String urlsuffix=null;
  private boolean jsgoGenerate=true;
  private int totalpage;

  public int getMaxIndexPages() {
    return maxIndexPages;
  }

  public void setMaxIndexPages(int maxIndexPages) {
    this.maxIndexPages = maxIndexPages;
  }

  public String getUrlprefix() {
    return urlprefix;
  }

  public void setUrlprefix(String urlprefix) {
    this.urlprefix = urlprefix;
  }

  public String getUrlsuffix() {
    return urlsuffix;
  }

  public void setUrlsuffix(String urlsuffix) {
    this.urlsuffix = urlsuffix;
  }

  public boolean isJsgoGenerate() {
    return jsgoGenerate;
  }

  public void setJsgoGenerate(boolean jsgoGenerate) {
    this.jsgoGenerate = jsgoGenerate;
  }

  final int getFirstIndexPage() {
    int firstPage;
    int halfIndexPages = maxIndexPages / 2;

    // put the current page in middle of the index
    firstPage = Math.max(1, currentpage - halfIndexPages);
    return firstPage;
  }

  final int getLastIndexPage(int firstPage) {
    int halfIndexPages = maxIndexPages / 2;
    int lastpage;
    if (currentpage < halfIndexPages) {
      lastpage=maxIndexPages;
    } else {
      lastpage = firstPage + maxIndexPages;
    }
    if (lastpage>totalpage)
      lastpage=totalpage;
    return lastpage;
  }

  public String generateURL(int newpage) {
    if (url!=null)
      return String.format(url, newpage);
    else
      return urlprefix+newpage+urlsuffix;
  }
  public boolean isValidPage(int pagenumber) {
    return (pagenumber > 0) && (pagenumber <= totalpage);
  }


  public final int getTotalpage() {
    return totalpage;
  }

  public final void setTotalpage(int totalpage) {
    this.totalpage = totalpage;
  }

  public final String getUrl() {
    return url;
  }

  public final int getTotal() {
    return total;
  }

  public final int getPagesize() {
    return pagesize;
  }

  public final int getCurrentpage() {
    return currentpage;
  }

  public final void setUrl(String url) {
    this.url = url;
  }

  public final void setTotal(int total) {
    this.total = total;
  }

  public final void setPagesize(int pagesize) {
    this.pagesize = pagesize;
  }

  public final void setCurrentpage(int currentpage) {
    this.currentpage = currentpage;
  }

}
