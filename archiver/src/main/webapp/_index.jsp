<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.springframework.web.context.WebApplicationContext" %>
<%@ page import="org.kbs.archiver.persistence.FrontPageMapper" %>
<%@ page import="java.util.List" %>
<%@ page import="org.kbs.archiver.ThreadEntity" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.apache.struts2.ServletActionContext" %>
<%@ page language="java" pageEncoding="UTF-8" session="false" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">
<%!
    String getFixedWidthString(String str,int width) {
        if (str.length()*2<width)
            return str;
        int i;
        int curwidth=0;
        for (i=0;i<str.length();i++) {
            if (str.charAt(i)<0x7f)
                curwidth++;
            else {
                curwidth+=2; //TODO two charpoint for one char
            }
            if (curwidth>=width)
                break;
        }
        return str.substring(0,i);
    }
    void showThreads(String name, List<ThreadEntity> threadlist, JspWriter out,boolean showsubjectonly) throws IOException {
        out.print("<h3>" + name + "</h3>");
        out.print("<table class=\"table-bordered\" width=\"100%\">");
        if (!showsubjectonly)
            out.print("<thead><td width=\"20%\"><h4>版面</h4></td><td><h4>主题</h4></td><td><h4>回复数</h4></td></thead>");
        for (ThreadEntity thread : threadlist) {
            out.print("<tr>");
            if (!showsubjectonly)
                out.print("<td><a href=\"board-" + thread.getBoardid() + ".html\">[" + thread.getBoardname() + "]</a></td>");
            String subject=getFixedWidthString(thread.getSubject(),50);
            out.print("<td><a href=\"thread-" + thread.getEncodingurl() + ".html\">" + subject + "</a></td>");
            if (!showsubjectonly)
                out.print("<td>" + thread.getArticlenumber()+"</td>");
            out.print("</tr>");
        }
        out.print("</table>");
    }
%>
<html>
<head>
    <title>水木社区归档站</title>
    <meta http-equiv="cache-control" content="max-age=1200;">
    <meta http-equiv="expires" content="<%
    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //use iso8601 time format.....maybe change to rfc2616?
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    out.print(format.format(new Date(System.currentTimeMillis()+1000*300)));
%>">
    <meta http-equiv="description" content="水木社区归档站首页">
    <jsp:include page="include/htmlheader.jsp"/>
</head>
<body>
<s:set var="nobreadcrumbs" value="'true'" scope="request" />
<jsp:include page="include/header.jsp"/>
<div class="container container-fluid">
    <div class="row-fluid">
        <div class="span9">
            <%

                WebApplicationContext webApplicationContext = WebApplicationContextUtils
                        .getRequiredWebApplicationContext(ServletActionContext
                                .getServletContext());
                FrontPageMapper frontPageMapper = (FrontPageMapper) webApplicationContext.getBean("frontpageMapper");
                List<ThreadEntity> articleList = frontPageMapper.getHotThreads(null,20);
                showThreads("热门帖子", articleList, out,false);
                List<String> groups=frontPageMapper.getGroups();
                for (String group:groups) {
                    articleList=frontPageMapper.getHotThreads(group,10);
                    showThreads(group+"区热帖",articleList,out,false);
                }
            %>
            <!--Body content-->
        </div>
        <div class="span3">
            <!--Sidebar content-->
            <%
                articleList=frontPageMapper.getNewestThreads(30);
                showThreads("最新帖子",articleList,out,true);
            %>
        </div>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>
