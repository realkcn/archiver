<%@ page language="java" import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap" pageEncoding="UTF-8"  session="false" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="pg" uri="/WEB-INF/pager.tld"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<s:set var="docbase" value="'../'" scope="request" />
<html>
<head>

    <title>水木社区作者文章列表</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="description" content="水木社区删除文章列表">

    <link rel="stylesheet" type="text/css" href="css/ansi.css" />

    <jsp:include page="../include/htmlheader.jsp" />
</head>

<body>
<s:set var="pagedetail" value="已删除文章列表" scope="request" />
<jsp:include page="../include/header.jsp" />
<div class="container">
    <pg:pager total="${totalsize}" urlprefix="showDeleted.do?pageno=" urlsuffix="" currentpage="${pageno}" pagesize="${pagesize}" />

    一共删除了 ${totalsize} 篇文章 <br />
    <jsp:include page="../include/pagerindex.jsp" />
    <table border="1">
        <s:set var="columncount" value="'6'" scope="page"/>
        <s:iterator value="articlelist" status="indexcount">
            <tr><td>${cname}</td>
                <td><a href="/abyu-${author}.html">${author}</a></td>
                <td><s:property value="subject"/></td>
                <td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>
                <td>删除人<s:property value="deleteby"/></td>
                <td>删除时间<s:date name="deletetime" format="yyyy-MM-dd HH:mm:ss" /></td>
            </tr>
            <tr>
                <td colSpan="${pageScope.columncount}"><s:set name="body" value="body"
                                                              scope="request" />
                    <%
                        out.print(org.kbs.archiver.util.AnsiToHtml
                                .ansiToHtml((String) request.getAttribute("body")));
                    %><br />
                    <br />
                    <s:if test="attachments!=null">
                        <s:iterator value="attachments">
                            <s:set name="filename" value="name" scope="request" />
                            <%
                                {
                                    String contentType = new MimetypesFileTypeMap()
                                            .getContentType(((String) request
                                                    .getAttribute("filename"))
                                                    .toLowerCase());
                                    if (contentType.startsWith("image")) {
                            %><img src="att/${encodingurl}/<s:property value="name" />" />
                            <%
                            } else {
                            %>附件:<a
                            href="att/${encodingurl}/<s:property value="name" />"
                            target="_blank"><s:property value="name" /></a>(大小:${datasize}字节)<br />
                            <%
                                    }
                                }
                            %>
                        </s:iterator>
                    </s:if><input type="hidden" value="${encodingurl}" /></td>
            </tr>
        </s:iterator>
    </table>

    <jsp:include page="../include/pagerindex.jsp" />
    <jsp:include page="../include/footer.jsp" />
</div>
</body>
</html>
