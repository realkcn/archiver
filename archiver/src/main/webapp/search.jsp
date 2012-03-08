<%@ page language="java"
	import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>水木社区作者文章列表</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="description" content="水木社区文章搜索">

<link rel="stylesheet" type="text/css" href="css/ansi.css" />

<jsp:include page="include/htmlheader.jsp" />
</head>
<body>
<s:set var="pagedetail" value="'搜索'" scope="request" />
	<jsp:include page="include/header.jsp" />
    <div class="container">
    <form class="form-horizontal well" action="searchArticle.do" method="GET">
        <fieldset>
            <div class="control-group">
            <label class="control-label" for="subject">标题</label>
              <div class="controls">
                <input type="text" class="input-xlarge" id="subject" name="subject" value="<s:property value="subject" />" />
              </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="author">作者 ID</label>
                <div class="controls">
                <input type="text" class="input-xlarge" id="author" name="author" value="<s:property value="author" />"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="boardname">版面名称</label>
                <div class="controls">
                <input type="text" class="input-xlarge" id="boardname" name="boardname" value="<s:property value="boardname" />"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="body">内容包含：</label>
                <div class="controls">
                <input type="text" class="input-xlarge" id="body" name="body" value="<s:property value="body" />"/>
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="start">发表时间：</label>
                <div class="controls">
                <input type="text" class="input-medium" id="start" name="start" value="<s:property value="start" />"/>至<input class="input-medium" type="text" id="end" name="end" value="<s:property value="end" />"/>
                </div>
                <p class="help-block">格式为YYYYMMDD，比如20120130</p>
            </div>
            <div class="form-actions">
                <input type="submit" class="btn btn-primary" value="搜索" />
                <input type="reset" class="btn btn-primary" value="重置" />
            </div>
        </fieldset>
    </form>
	<br />
	<s:actionerror cssClass="label label-important" />
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
