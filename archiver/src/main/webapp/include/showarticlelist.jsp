<%@ page language="java"
         session="false"
         import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap"
         pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<table border="1">
<s:if test="board==null">
    <s:set var="columncount" value="'4'" scope="page"/>
</s:if>
<s:else>
    <s:set var="columncount" value="'3'" scope="page"/>
</s:else>
<s:iterator value="articlelist" status="indexcount">
    <s:if test="#indexcount.first==true">
        <tr><td colspan="${pageScope.columncount}">
            <script type="text/javascript">/*728*90，读帖页*/ var cpro_id = 'u825487';</script><script src="http://cpro.baidu.com/cpro/ui/c.js" type="text/javascript"></script>
        </td>
        </tr>
    </s:if>
    <tr><s:if test="boardname!=null">
        <td><a href="board-${boardid}.html">${boardname}</a></td></s:if>
        <td><a href="abyu-${author}.html">${author}</a></td>
        <td><s:if test="thread==null"><a href="searchThreadByAURL.do?aurl=${encodingurl}"><s:property value="subject"/></a></s:if>
            <s:else><s:property value="subject"/></s:else>
        </td>
        <td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>
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