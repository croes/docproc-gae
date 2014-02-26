<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1" />
<title>Docproc document processing</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bootstrap.css" />
<script src="${pageContext.request.contextPath}/js/jquery-1.10.2.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
<script src="${pageContext.request.contextPath}/js/jquery.csv-0.71.js"></script>
<script src="${pageContext.request.contextPath}/js/docproc.js"></script>
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
					<li class="active"><a href="addjob.jsp">Add job</a></li>
					<li><a href="${pageContext.request.contextPath}/history.jsp">History</a></li>
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

	<div class="container">
		<h1>Docproc</h1>
		<form id="startForm">
			<label for="templateFile">Template file (XSL-FO, UTF-8,
				reference data variables with ${variable})</label> <input id="templateFile"
				type="file" class="form-control" />
			<output class="collapse" id="outTemplate" for="template">
				<label for="template">Template contents</label>
				<textarea class="form-control" id="template" rows="10" cols="100"
					name="template"></textarea>
			</output>
			<label for="dataFile">Data (CSV format, ';' seperator, UTF-8,
				header=variable names, rows=variable data)</label> <input id="dataFile"
				type="file" class="form-control" />
			<output id="outData" class="collapse" for="dataFile">
				<label for="summary">Summary</label>
				<ul id="summary" class="list-group">
					<li class="list-group-item"><span
						class="toggler glyphicon glyphicon-chevron-down"
						data-toggle="collapse" data-target="#variables"></span> <span
						id="variablesBadge" class="badge">0</span> Template variables</li>
					<div id="variables" class="collapse">
						<ul id="variablesList">
						</ul>
					</div>
					<li class="list-group-item"><span
						class="toggler glyphicon glyphicon-chevron-down"
						data-toggle="collapse" data-target="#records"></span> <span
						id="recordsBadge" class="badge">0</span> Records</li>
					<div id="records" class="collapse">
						<ul id="recordsList">
						</ul>
					</div>
				</ul>
				<label for="data">CSV data contents</label>
				<textarea class="form-control" id="data" rows="10" cols="100"
					name="data"></textarea>
			</output>
			<label for="finishBefore">Finish before (optional)</label> <input
				id="finishBefore" type="date" class="form-control"
				placeholder="dd/MM/yyyy" name="finishBefore" /> <label
				for="startAfter">Start after (optional)</label> <input
				id="startAfter" type="date" class="form-control"
				placeholder="dd/MM/yyyy" name="startAfter" />
			<div style="text-align: center; margin-top: 10px;">
				<button id="submitbtn" class="btn btn-default btn-lg" type="button">Submit</button>
			</div>
		</form>
	</div>
</body>
</html>
