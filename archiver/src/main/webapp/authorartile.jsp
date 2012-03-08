<%@ page language="java" import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
 <%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
  <head> 
    
    <title>水木社区作者文章列表</title> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区作者文章列表"> 
  
 <link rel="stylesheet" type="text/css" href="css/ansi.css" />
 
<jsp:include page="include/htmlheader.jsp" />
  </head> 
  
  <body>
  <s:set var="pagedetail" value="'${author}文章列表'" scope="request" />
  <jsp:include page="include/header.jsp" />
  <div class="container">
   <pg:pager total="${totalsize}" urlprefix="abyu-${author}-" urlsuffix=".html" currentpage="${pageno}" pagesize="${pagesize}" />
 
  ${author} 一共发表了 ${totalsize} 篇文章 <br />
<jsp:include page="include/pagerindex.jsp" />
     <jsp:include page="include/showarticlelist.jsp" />
<jsp:include page="include/pagerindex.jsp" />
  <jsp:include page="include/footer.jsp" />
  </div>
  </body> 
</html> 