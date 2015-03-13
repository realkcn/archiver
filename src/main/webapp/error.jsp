<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" session="false"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
  <head> 
    
    <title>出错了</title> 
    
 <meta http-equiv="pragma" content="no-cache"> 
 <meta http-equiv="cache-control" content="no-cache"> 
 <meta http-equiv="expires" content="0">    
 <meta http-equiv="description" content="错误页面"> 
<jsp:include page="include/htmlheader.jsp" />
   </head> 
  
  <body>
  <jsp:include page="include/header.jsp" />
<s:actionerror/>
   <jsp:include page="include/footer.jsp" />
   </body> 
</html> 