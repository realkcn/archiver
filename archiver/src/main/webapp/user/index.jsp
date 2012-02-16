<%@page import="org.jasig.cas.client.authentication.AttributePrincipal"%>
<%
	AttributePrincipal principal = (AttributePrincipal) request
			.getUserPrincipal();
	String username = principal.getName();
	out.println(username);
%>
<a href="http://sso.newsmth.net/cas/logout">Logout</a>