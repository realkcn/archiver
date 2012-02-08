<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
<table>
<tr>
<td width="30%">
<a href="http://www.newsmth.net">水木社区站</a> ** <a href="${request.docbase}boards.html">归档站</a><s:if test="board!=null">-><a href="${request.docbase}board-${board.boardid}.html">${board.cname}</a></s:if><br />
</td>
<td align="right" width="70%">
<s:if test="!#request.nosearchframe==true">
<jsp:include page="searchframe.jsp" />
</s:if>
</td>
</table>