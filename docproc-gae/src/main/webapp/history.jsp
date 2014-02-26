<%@page import="be.gcroes.thesis.docproc.gae.entity.OfyService"%>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="be.gcroes.thesis.docproc.gae.entity.Job" %>
<%@ page import="static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css" />
    <script src="${pageContext.request.contextPath}/js/jquery-1.10.2.js"></script>
    <script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
<title>Docproc</title>
</head>
<body style="padding-top: 50px;">
	<%
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
      pageContext.setAttribute("user", user);
    } else {
    	response.sendRedirect(userService.createLoginURL(request.getRequestURI()));
    }
	%>
	<nav class="navbar navbar-default navbar-fixed-top navbar-inverse"
		role="navigation">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="${pageContext.request.contextPath}/index.jsp">Docproc</a>
		</div>
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="${pageContext.request.contextPath}/addjob.jsp">Add job</a></li>
				<li class="active"><a href="${pageContext.request.contextPath}/history.jsp">History</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="<%userService.createLogoutURL(request.getRequestURI()); %>">Logout</a></li>
			</ul>
			<p class="navbar-text navbar-right">
				<span class="glyphicon glyphicon-user"></span>${fn:escapeXml(user.nickname)}
			</p>
		</div>
	</div>
	</nav>
	<div class="container">
	<h2>Jobs for ${fn:escapeXml(user.nickname)}</h2>
	<table class="table table-striped table-bordered table-condensed">
		<tr>
		<td>Job id</td>
		<td>Started on</td>
		<td>Finished on</td>
		<td>Duration</td>
		<td>Number of tasks</td>
		<td>Details</td>
		<td>Download</tr>
		<%
			SimpleDateFormat dt = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			List<Job> jobs = ofy().load().type(Job.class).filter("user =", user.getUserId()).list();
	        for(Job job : jobs){
	            String startedOn = "Not started", finishedOn = "Not finished", duration = "NA";
	            if(job.getStartTime() != null)
	            	startedOn = dt.format(job.getStartTime());
	            if(job.getEndTime() != null)
	            	finishedOn = dt.format(job.getEndTime());
	            duration = (job.getDurationInMillis()) / 1000L + " seconds";
	            %>
	           <tr>
	           	<td> <%=job.getId()%> </td>
	           	<td> <%=startedOn%> </td>
	           	<td> <%=finishedOn%> </td>
	           	<td> <%=duration %> </td>
	           	<td> <%=job.getTasks().size()%></td>
	           	<td> <a href="${pageContext.request.contextPath}/details.jsp?jobId=<%=job.getId()%>">Details</a></td>
	           	<td> <a href="${pageContext.request.contextPath}/download?jobId=<%=job.getId()%>">Download zip</a></td>
	           </tr> 
	        <% }
		%>
	</table>
		<% if(jobs.size() == 0){
		    %> <p>No jobs found</p> 
		<% }%>
</div>
</body>
</html>