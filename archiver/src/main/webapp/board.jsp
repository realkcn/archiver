<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
  <head> 
    
    <title>水木社区版面主题列表</title> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区主题列表"> 
  
 
  </head> 
    <pg:pager total="${totalsize}" url="board-${boardid}-%d.html" currentpage="${pageno}" pagesize="${pagesize}" />

  <body> 
   <jsp:include page="header.jsp" />
    <pg:first>第一页</pg:first><pg:prev>上一页</pg:prev><pg:pages>  
                    <s:if test="#attr.currentPageNumber==#attr.pageNumber">    
                       <font color="red">${attr.pageNumber}</font>  
                    </s:if><s:else>   
                       <pg:go newpage="${attr.pageNumber}">${attr.pageNumber}</pg:go>
                    </s:else>
               </pg:pages><pg:next>下一页</pg:next><pg:last>最后一页</pg:last>
   <table border="1"> <caption>版面主题列表</caption>
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
  <pg:first>第一页</pg:first><pg:prev>上一页</pg:prev><pg:pages>  
                    <s:if test="#attr.currentPageNumber==#attr.pageNumber">    
                       <font color="red">${attr.pageNumber}</font>  
                    </s:if><s:else>   
                       <pg:go newpage="${attr.pageNumber}">${attr.pageNumber}</pg:go>
                    </s:else>
               </pg:pages><pg:next>下一页</pg:next><pg:last>最后一页</pg:last>
   </body> 
</html> 