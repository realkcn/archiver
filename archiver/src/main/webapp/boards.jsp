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
   <table border="1" > <caption>版面列表</caption>
       <tr><td colspan="3"></td><td colspan="2"><a href="search.jsp">搜索</a></td></tr>
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
  <jsp:include page="include/footer.jsp" />
  </body> 
</html> 
