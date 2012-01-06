<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
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
   <pg:pager total="${totalsize}" url="abyu-${author}-%d.html" currentpage="${pageno}" pagesize="${pagesize}" />
 
  ${author} 一共发表了 ${totalsize} 篇文章 <br />
  <pg:first>第一页</pg:first><pg:prev>上一页</pg:prev><pg:pages>  
                    <s:if test="#attr.currentPageNumber==#attr.pageNumber">    
                       <font color="red">${attr.pageNumber}</font>  
                    </s:if><s:else>   
                       <pg:go newpage="${attr.pageNumber}">${attr.pageNumber}</pg:go>
                    </s:else>
               </pg:pages><pg:next>下一页</pg:next><pg:last>最后一页</pg:last>
 <table border="1"> <caption>作者文章列表</caption>
<!-- 
   <tr>
   <td>序号</td>
   <td>作者</td>
   <td>主题</td>
   <td>发表时间</td>
   </tr>
 -->   
   <s:iterator value="articlelist" status="threadindex">
    <tr>
<!--		<td><s:property value="#threadindex.count+(pageno-1)*pagesize" /></td> 
-->
		<td>${subject}</td>      
		<td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>
	</tr>
	<tr>
		<td><s:set name="body" value="body" scope="request" /><%
			out.print(org.kbs.archiver.util.AnsiToHtml.ansiToHtml((String)request.getAttribute("body")));
		%><br /></td>
    </tr>
    
   </s:iterator>
    </table>
  <pg:first>第一页</pg:first><pg:prev>上一页</pg:prev><pg:pages>  
                    <s:if test="#attr.currentPageNumber==#attr.pageNumber">    
                       <font color="red">${attr.pageNumber}</font>  
                    </s:if><s:else>   
                       <pg:go newpage="${attr.pageNumber}">${attr.pageNumber}</pg:go>
                    </s:else>
               </pg:pages><pg:next>下一页</pg:next><pg:last>最后一页</pg:last>
  </body> 
</html> 