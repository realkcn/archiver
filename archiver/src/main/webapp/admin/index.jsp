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
	document.getElementById("show").src="../abyu-"+document.getElementById("author").value+".html";
}
</script>
<body>
<s:if test="#showurl==null">
<s:set name="showurl" value="" />
</s:if>
<s:actionerror/>
<s:actionmessage/>
<form action="deleteArticle" method="GET">
文章ID<input type="text" id="articleid" name="articleid" /><input  type="button" onclick="loadarticle()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<hr />
<form action="deleteThread" method="GET">
主题ID<input type="text" id="threadid" name="threadid"/><input  type="button" onclick="loadthread()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<hr />
<form action="deleteByAuthor" method="GET">
作者ID<input type="text" id="author" name="author"/><input  type="button" onclick="loadauthor()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<hr />
<form action"deleteArticleByOriginId" method="GET">
通过BBS原文ID删除
文章版面<input type="text" id="boardname" name="boardname"/>文章ID<input type="text" id="articleid" name="articleid" /><input  type="button" onclick="javascript:submit()" value="搜索" /><input type="button" value="删除" onclick="javascript:if(confirm('确定删除？')){submit()}"/>
</form>
<br/>
<iframe width="100%" height="80%"  scroll="true" name="show"  id="show" src="${showurl}"/>
</body>
</html>
