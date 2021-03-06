<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" session="false"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head> 
    
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区主题列表"> 
  
 
<jsp:include page="include/htmlheader.jsp" />
  <title>水木社区归档站 - ${board.name}/${board.cname}</title> 
  </head>
    <pg:pager total="${totalsize}" urlprefix="board-${boardid}-" urlsuffix=".html" currentpage="${pageno}" pagesize="${pagesize}" />

  <body>
<s:set var="pagedetail" value="'文章列表'" scope="request" />
<jsp:include page="include/header.jsp" />
<div class="container">
<jsp:include page="include/pagerindex.jsp" />
   <table class="table table-bordered">
   <tr>
   <td>序号</td>
   <td>作者</td>
   <td>主题</td>
   <td>发表时间</td>
   <td>回复数</td>
   <td>最后回复者</td>
   <td>最后回复时间</td>
   </tr>
   <s:iterator value="threadlist" status="threadindex">
    <tr>
		<td><s:property value="#threadindex.count+(pageno-1)*pagesize" /></td>
		<td><a href="abyu-${author}.html">${author}</a></td>      
		<td><a href="thread-${encodingurl}.html"><s:property value="subject" /></a></td>      
		<td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>      
		<td>${articlenumber-1}</td>      
		<td>${lastreply}</td>      
		<td><s:date name="lastposttime" format="yyyy-MM-dd HH:mm:ss" /></td>      
    </tr>
   </s:iterator>
    </table>
<jsp:include page="include/pagerindex.jsp" />
</div>
  <jsp:include page="include/footer.jsp" />
   </body> 
</html> 
