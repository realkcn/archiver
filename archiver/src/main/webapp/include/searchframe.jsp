<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<form  class="navbar-search pull-right" action="${request.docbase}searchArticle.do" method="GET">
<input class="search-query" placeholder="Search" id="search_input" type="text" value="" name="body" /><input class="btn" type="submit" value="搜索"/></form>
</form>
