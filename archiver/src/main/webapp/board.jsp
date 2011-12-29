<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
  <head> 
    
    <title>水木社区版面主题列表</title> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区主题列表"> 
  
 
  </head> 
  
  <body> 
   <table border="1"> <caption>版面列表</caption>
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
		<td><a href="thread_${encodingurl}.html">${author}</a></td>      
		<td><a href="thread_${encodingurl}.html">${subject}</a></td>      
		<td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>      
		<td>${articlenumber-1}</td>      
		<td>${lastreply}</td>      
		<td><s:date name="lastposttime" format="yyyy-MM-dd HH:mm:ss" /></td>      
    </tr>
   </s:iterator>
    </table>
    <s:if test="pageno!=1">
      <a href="board-${boardid}.html">第一页</a>
      <a href="board-${boardid}-${pageno-1}.html">上一页</a>
    </s:if>
    <s:if test="pageno!=totalpage">
      <a href="board-${boardid}-${pageno+1}.html">下一页</a>
      <a href="board-${boardid}-${totalpage}.html">最后一页</a>
    </s:if>
  </body> 
</html> 