<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%> 
<%@taglib prefix="s" uri="/struts-tags"%>
<a href="http://www.newsmth.net">水木社区站</a> ** <a href="boards.html">归档站</a><s:if test="board!=null">-><a href="board-${board.boardid}.html">${board.cname.substring(11)}</a></s:if><br />
