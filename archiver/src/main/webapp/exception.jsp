<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
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
	<h3 style="color: red">
		<!-- 获得异常对象 -->
		<s:property value="exception.message" />
	</h3>
	<br />
	<!-- 异常堆栈信息 -->
	<s:property value="exceptionStack" />
	<jsp:include page="include/footer.jsp" />
</body>
</html>
