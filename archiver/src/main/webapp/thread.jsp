<%@page language="java" import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
  <head> 
    
    <title>水木-${board.cname.substring(11)}-<s:property value="thread.subject" /></title> 
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区主题文章列表"> 
 
 <link rel="stylesheet" type="text/css" href="css/ansi.css" />
  
</head> 
  
  <body>
  <jsp:include page="header.jsp" />
  <pg:pager total="${totalsize}" urlprefix="thread-${tid}-" urlsuffix=".html" jsgoGenerate="true" currentpage="${pageno}" pagesize="${pagesize}" />
  一共 ${totalsize} 篇 <br />
<jsp:include page="include/pagerindex.jsp" />
<!-- 
   <tr>
   <td>序号</td>
   <td>作者</td>
   <td>主题</td>
   <td>发表时间</td>
   </tr>
 -->   
   
   <table border="1"> <caption>主题文章列表</caption>
    <s:iterator value="articlelist" status="threadindex">
    <tr>
<!--		<td><s:property value="#threadindex.count+(pageno-1)*pagesize" /></td> 
-->
		<td rowSpan="2" ><a href="abyu-${author}.html">${author}</a></td>
		<td><s:property value="subject" /></td>      
		<td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>
	</tr>
	<tr><td colSpan="2"><s:set name="body" value="body" scope="request" /><%
			out.print(org.kbs.archiver.util.AnsiToHtml.ansiToHtml((String)request.getAttribute("body")));
		%><br /><s:if test="attachments!=null">
		   <s:iterator value="attachments"><s:set name="filename" value="name" scope="request" /><% {
		   		String contentType = new MimetypesFileTypeMap().getContentType(((String)request.getAttribute("filename")).toLowerCase());
		   		if (contentType.startsWith("image")) {
		   %><img src="att-${encodingurl}/<s:property value="name" />" /><%} else {%>附件:<a href="att-${encodingurl}/<s:property value="name" />" target="_blank"><s:property value="name" /></a>(大小:${datasize}字节)<br /><% } }%>
		   </s:iterator>
		</s:if>
		</td>
	</tr>
   </s:iterator>
    </table>
<jsp:include page="include/pagerindex.jsp" />
  </body> 
</html> 
