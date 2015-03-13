<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" session="false"%>
<%@ page isErrorPage="true" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>出错了</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="description" content="错误页面">
</head>

<body>
	<%
		if (exception != null) {
//				System.err.println(exception.getMessaage());
				exception.printStackTrace();
		}
    %>
</body>
</html>
