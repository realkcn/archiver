<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="http://www.newsmth.net">水木社区主站</a>
            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active">
            <a href="${request.docbase}boards.html">归档站首页</a>
                    </li>
                    <li class="active">
            <a class="active" href="${request.docbase}search.jsp">搜索</a>
                    </li>
                </ul>
            </div>
                <form  class="navbar-search pull-right" action="${request.docbase}searchArticle.do" method="GET">
                    <input class="search-query" placeholder="" id="search_input" type="text" value="" name="body" />
                    <input class="btn" type="submit" value="搜索"/></form>
                </form>
        </div>
    </div>
</div>
<div class="container">
    <ul class="breadcrumb">
        <li>
            <a href="${request.docbase}boards.html">首页</a> <span class="divider">/</span>
        </li>
        <s:if test="board!=null">
        <li>
            <a href="${request.docbase}board-${board.boardid}.html">${board.cname}</a> <span class="divider">/</span>
        </li>
        </s:if>
        <li class="active">
            <a href="#">${request.pagedetail}</a>
        </li>
    </ul>
</div>