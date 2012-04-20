<%@page import="org.kbs.sso.principal.AttributePrincipal"%>
<%
	AttributePrincipal principal = (AttributePrincipal) request
			.getUserPrincipal();
	String username = principal.getName();
	out.println(username);
%>
<a href="http://sso.newsmth.net/cas/logout">Logout</a>