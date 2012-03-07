<%@ page language="java"
	import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap"
	pageEncoding="UTF-8"%>
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

<jsp:include page="include/htmlheader.jsp" />
<s:set name="nosearchframe" scope="request" value="true" />
</head>
<body>
<div class="container">
	<jsp:include page="include/header.jsp" />
    <form action="searchArticle.do" method="GET">
        <table style="table-layout:fixed">
            <tr>
                <td width="30%">标题</td>
                <td width="70%"><input type="text" name="subject" value="<s:property value="subject" />" /></td>
            </tr>
            <tr>
                <td>作者 ID</td>
                <td><input type="text" name="author" value="<s:property value="author" />"/></td>
            </tr>
            <tr>
                <td>版面名称</td>
                <td><input type="text" name="boardname" value="<s:property value="boardname" />"/></td>
            </tr>
            <tr>
                <td>内容包含：</td>
                <td><input type="text" name="body" value="<s:property value="body" />"/></td>
            </tr>
            <tr>
                <td>发表时间(YYYYMMDD)：</td>
                <td><input type="text" name="start" value="<s:property value="start" />"/>至<input type="text" name="end" value="<s:property value="end" />"/></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="搜索" /></td>
            </tr>
        </table>
    </form>
	<br />
	<s:actionerror />
	<s:if test="articlelist!=null">
		<pg:pager total="${totalsize}"
			urlprefix="searchArticle.do?boardname=${boardname}&body=${body}&author=${author}&start=${start}&end=${end}&subject=${subject}&pageno=" urlsuffix=""
			jsgoGenerate="true" currentpage="${pageno}" pagesize="${pagesize}" />
  一共 ${totalsize} 篇 <br />
  <s:if test="totalsize>0">
		<jsp:include page="include/pagerindex.jsp" />
            <s:set name="noadsense" scope="request" value="true" />
            <jsp:include page="include/showarticlelist.jsp" />
		<jsp:include page="include/pagerindex.jsp" />
		</s:if>
	</s:if>
	<jsp:include page="include/footer.jsp" />
</div>
</body>
</html>
