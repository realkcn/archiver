<%@page language="java"  session="false" import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
  <head> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区主题文章列表"> 
 
 <link rel="stylesheet" type="text/css" href="css/ansi.css" />
  
<jsp:include page="include/htmlheader.jsp" />
  <title>水木-${board.cname}-<s:property value="thread.subject" /></title> 
</head> 
  
  <body>
  <s:set var="pagedetail" value="'主题文章列表'" scope="request" />
  <jsp:include page="include/header.jsp" />
  <div class="container">
  <pg:pager total="${totalsize}" urlprefix="thread-${tid}-" urlsuffix=".html" jsgoGenerate="true" currentpage="${pageno}" pagesize="${pagesize}" />
  一共 ${totalsize} 篇 <a href="http://www.newsmth.net/nForum/article/${board.name}/${thread.originid}" target="_blank">回主站参与讨论</a><br />
<jsp:include page="include/pagerindex.jsp" />
<!-- 
   <tr>
   <td>序号</td>
   <td>作者</td>
   <td>主题</td>
   <td>发表时间</td>
   </tr>
 -->   
   
<jsp:include page="include/showarticlelist.jsp" />
<jsp:include page="include/pagerindex.jsp" />
  <jsp:include page="include/footer.jsp" />
  </div>
  </body> 
</html> 
