<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
<form action="${request.docbase}searchArticle.do" method="GET">
<input id="search_input" type="text" value="" name="body" /><input type="submit" value="搜索"/></form>
</form>
