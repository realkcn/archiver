<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>水木社区归档管理</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
</head>
<script language="javascript" type="text/javascript">
function loadarticle()
{
	document.getElementById("show").src="../article-"+document.getElementById("articleid").value+".html";
}
function loadthread()
{
	document.getElementById("show").src="../thread-"+document.getElementById("threadid").value+".html";
}
function loadauthor()
{
	document.getElementById("show").src="../abyu-"+document.getElementById("authorid").value+".html";
}
</script>
<body>
<s:actionerror/>
<s:actionmessage/>
<form action="deleteArticle" method="GET">
文章ID<input type="text" id="articleid" name="articleid" /><input  type="button" onclick="loadarticle()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<br />
<form action="deleteThread" method="GET">
主题ID<input type="text" id="threadid" name="threadid"/><input  type="button" onclick="loadthread()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<form action="deleteAuthor" method="GET">
作者ID<input type="text" id="authorid" name="authorid"/><input  type="button" onclick="loadauthor()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<br/>
<iframe width="100%" height="80%"  scroll="true" name="show"  id="show" src=""/>
</body>
</html>
