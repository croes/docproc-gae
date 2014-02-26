<%@page import="be.gcroes.thesis.docproc.gae.entity.OfyService"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="be.gcroes.thesis.docproc.gae.entity.Job"%>
<%@ page import="be.gcroes.thesis.docproc.gae.entity.Task"%>
<%@ page
	import="static be.gcroes.thesis.docproc.gae.entity.OfyService.ofy"%>
<%@ page import="com.googlecode.objectify.Key"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@page import="java.text.SimpleDateFormat"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bootstrap.css" />
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
			response.sendRedirect(userService.createLoginURL(request
					.getRequestURI()));
		}
	%>
	<nav class="navbar navbar-default navbar-fixed-top navbar-inverse"
		role="navigation">
	<div class="container">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse"
				data-target="#bs-example-navbar-collapse-1">
				<span class="sr-only">Toggle navigation</span> <span
					class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand"
				href="${pageContext.request.contextPath}/index.jsp">Docproc</a>
		</div>
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="${pageContext.request.contextPath}/addjob.jsp">Add
						job</a></li>
				<li class="active"><a
					href="${pageContext.request.contextPath}/history.jsp">History</a></li>
			</ul>
			<ul class="nav navbar-nav navbar-right">
				<li><a href="${pageContext.request.contextPath}/logout.jsp">Logout</a></li>
			</ul>
			<p class="navbar-text navbar-right">
				<span class="glyphicon glyphicon-user"></span>${fn:escapeXml(user.nickname)}
			</p>
		</div>
	</div>
	</nav>
	<%
		Long jobId = Long.parseLong(request.getParameter("jobId"));
		Key<Job> key = Key.create(Job.class, jobId);
		Job job = ofy().load().key(key).now();
	%>
	<div class="container">
		<%
			if (job != null) {
		%>
		<h2>
			Details for job
			<%=request.getParameter("jobId")%></h2>
		<%
			SimpleDateFormat dt = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
				String startedOn = "Not started.";
				if (job.getStartTime() != null)
					startedOn = dt.format(job.getStartTime());
				String finishedOn = "Not finished yet.";
				if (job.getEndTime() != null)
					finishedOn = dt.format(job.getEndTime());
				String duration = (job.getDurationInMillis() / 1000f)
						+ " seconds";
		%>
		<h3>Job info</h3>
		<table class="table table-striped table-bordered table-condensed">
			<tr>
				<td>ID</td>
				<td><%=job.getId()%></td>
			</tr>
			<tr>
				<td>Started on</td>
				<td><%=startedOn%></td>
			</tr>
			<tr>
				<td>Finished on</td>
				<td><%=finishedOn%></td>
			</tr>
			<tr>
				<td>Duration</td>
				<td><%=duration%></td>
			</tr>
			<tr>
				<td>Number of tasks</td>
				<td><%=job.getTasks().size()%></td>
			</tr>
			<tr>
				<td>Template</td>
				<td><%=job.getTemplate()%></td>
			</tr>
			<tr>
				<td>Input data</td>
				<td><%=job.getCsv()%></td>
			</tr>
			<tr>
				<td>Job result</td>
				<td><a
					href="${pageContext.request.contextPath}/download?jobId=<%=job.getId()%>">Download
						zip</a></td>
			</tr>
		</table>
		<h3>Tasks</h3>
		<table class="table table-striped table-bordered table-condensed">
			<tr>
				<td>ID</td>
				<td>Param values</td>
				<td>Result</td>
			</tr>
			<%
				for (Task task : job.getTasks()) {
			%>
			<tr>
				<td><%=task.getId()%></td>
				<td>
					<ul>
						<%
							for (Map.Entry<String, Object> e : task.getParams()
											.entrySet()) {
						%>
						<li><%=e.getKey()%>: <%=e.getValue().toString()%></li>
						<%
							}
						%>
					</ul>
				<td><a
					href="${pageContext.request.contextPath}/download?jobId=<%=job.getId()%>&taskId=<%=task.getId()%>">Download</a></td>
			</tr>
			<%
				}
			%>
		</table>
		<%
			if (job.getTasks().size() == 0) {
		%>
		<p>No tasks found</p>
		<%
			}
		%>
	</div>
	<%
		} else {
	%>
	<h1>
		Could not find job with id
		<%=jobId%> with key <%=key.getRaw().toString() %></h1>
	<%
		}
	%>
</body>
</html>