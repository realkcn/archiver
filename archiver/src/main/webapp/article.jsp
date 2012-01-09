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
 
  </head> 
  
  <body>
  <jsp:include page="header.jsp" />
 <table border="1">
 <!-- 
   <tr>
   <td>序号</td>
   <td>作者</td>
   <td>主题</td>
   <td>发表时间</td>
   </tr>
 -->   
   <s:iterator value="articlelist">
    <tr>
		<td>${author}</td>      
		<td>${subject}</td>      
		<td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>
	</tr>
	<tr>
		<td colSpan="3"><s:set name="body" value="body" scope="request" /><%
			out.print(org.kbs.archiver.util.AnsiToHtml.ansiToHtml((String)request.getAttribute("body")));
		%><br /><br /><s:if test="attachments!=null">
		   <s:iterator value="attachments"><s:set name="filename" value="name" scope="request" /><% {
		   		String contentType = new MimetypesFileTypeMap().getContentType(((String)request.getAttribute("filename")).toLowerCase());
		   		if (contentType.startsWith("image")) {
		   %><img src="att-${encodingurl}/<s:property value="name" />" /><%} else {%>附件:<a href="att-${encodingurl}/<s:property value="name" />" target="_blank"><s:property value="name" /></a>(大小:${datasize}字节)<br /><% } }%>
		   </s:iterator></s:if></td>
    </tr>
    </s:iterator>
  </table>
  </body> 
</html> 