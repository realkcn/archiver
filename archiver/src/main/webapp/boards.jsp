<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
  <head> 
    
    <title>水木社区归档站 - 版面列表</title> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区版面列表">

 <jsp:include page="include/htmlheader.jsp" />
  </head> 
  
  <body>
<s:set var="pagedetail" value="'版面列表'" scope="request" />
  <jsp:include page="include/header.jsp" />
  <div class="container">
   <table class="table table-bordered">
    <tr>
       <td>序号</td>
       <td>英文名</td>
       <td>中文名</td>
       <td>主题数</td>
       <td>文章数</td>
   <s:iterator value="boardlist" status="boardindex">
    <tr>
		<td><s:property value="#boardindex.count" /></td>
		<td><a href="board-${boardid}.html"><s:property value="name" /></a></td>      
		<td><a href="board-${boardid}.html"><s:property value="cname" /></a></td>      
		<td>${threads}</td>      
		<td>${articles}</td>      
    </tr>
   </s:iterator>
    </table>
  </div>
  <jsp:include page="include/footer.jsp" />
  </body> 
</html> 
