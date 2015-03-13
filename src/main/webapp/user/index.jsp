<%@ page session="false" %><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.kbs.sso.principal.AttributePrincipal"%>
<%@ page import="org.kbs.sso.client.SSOFilter" %>
<%
	AttributePrincipal principal = SSOFilter.getPrincipal(request);
	String username = principal.getName();
	out.println(username);
    out.println(principal.getAttributes());
%>
<a href="http://sso.newsmth.net/cas/logout">Logout</a>