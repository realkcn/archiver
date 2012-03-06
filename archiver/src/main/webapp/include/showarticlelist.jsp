<%@ page language="java"
         import="java.util.*,org.kbs.archiver.*,javax.activation.MimetypesFileTypeMap"
         pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<s:iterator value="articlelist">
    <tr><s:if test="boardname!=null">
        <td><a href="board-${boardid}.html">${boardname}</a></td></s:if>
        <td><a href="abyu-${author}.html">${author}</a></td>
        <td>${subject}</td>
        <td><s:date name="posttime" format="yyyy-MM-dd HH:mm:ss" /></td>
    </tr>
    <tr><s:if test="boardname!=null">
        <td colSpan="4"></s:if><s:else><td colspan="3"></s:else><s:set name="body" value="body"
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