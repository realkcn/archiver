<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
 
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html> 
  <head> 
    
    <title>水木社区版面列表</title> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="水木社区版面列表"> 
  
 
  </head> 
  
  <body> 
   <table border="1" width="360"> <caption>版面列表</caption>
   <s:iterator value="boardlist" status="boardindex">
    <tr>
		<td><s:property value="#boardindex.count" /></td>
		<td><a href="board-${boardid}.html"><s:property value="name" /></a></td>      
		<td><a href="board-${boardid}.html"><s:property value="cname" /></a></td>      
    </tr>
   </s:iterator>
    </table>
  </body> 
</html> 