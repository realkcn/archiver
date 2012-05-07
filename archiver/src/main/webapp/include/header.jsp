<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" session="false" %>
<%@ page import="org.kbs.sso.client.SSOFilter" %>
<%@ page import="org.kbs.sso.principal.AttributePrincipal" %>
<%@ page import="org.apache.commons.lang3.StringEscapeUtils" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <a class="brand" href="http://www.newsmth.net">水木社区主站</a>
            <div class="nav-collapse">
                <ul class="nav">
                    <li class="active">
                        <s:if test="#request.docbase==null">
                            <s:set var="docbase" value="'./'" scope="request" />
                        </s:if>
            <a href="${request.docbase}">归档站首页</a>
                    </li>
                    <li class="active">
                        <a href="${request.docbase}boards.html">版面列表</a>
                    </li>
                    <li class="active">
            <a class="active" href="${request.docbase}search.jsp">搜索</a>
                    </li>
                    <li class="active">
                        <%
                            AttributePrincipal principal=SSOFilter.getPrincipal(request);
                            if (principal!=null) {
                            %><a href="#"><%
                                String nickname=(String)principal.get("nickname");
                                if (!StringUtils.isEmpty(nickname))
                                    out.print(StringEscapeUtils.escapeHtml4(nickname));
                                else
                                    out.print("无名氏");
                        %>，你好&nbsp;|</a></li><li class="active"><a href="${request.docbase}user/logout.jsp">注销</a>
                        <%
                            } else {
                        %>
                        <a href="${request.docbase}user/login.jsp">登录</a>
                        <%  } %>
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
<s:if test="#request.nobreadcrumbs==null">
<div class="container">
    <ul class="breadcrumb">
            <a href="${request.docbase}">首页</a> <span class="divider">/</span>
        <s:if test="board!=null">
            <a href="${request.docbase}boards.html">版面列表</a> <span class="divider">/</span>
            <a href="${request.docbase}board-${board.boardid}.html">${board.cname}</a> <span class="divider">/</span>
        </s:if>
            ${request.pagedetail}<s:if test="board!=null"><a style="float: right" href="http://www.newsmth.net/nForum/board/${board.name}" target="_blank">回主站[${board.cname}]版</a>
    </s:if>
    </ul>
</div>
</s:if>