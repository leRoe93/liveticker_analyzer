<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<c:set var="AppBase" value="${pageContext.request.contextPath}"></c:set>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>DBAE Tutorium 07</title>
</head>
<!-- jQuery required by Bootstrap -->
<script type="text/javascript" src="${ AppBase }/bower_components/jquery/dist/jquery.min.js"></script>
<!-- Bootstrap CSS & JS -->
<link rel="stylesheet" type="text/css" href="${ AppBase }/bower_components/bootstrap/dist/css/bootstrap.min.css">
<script type="text/javascript" src="${ AppBase }/bower_components/bootstrap/dist/js/bootstrap.min.js"></script>
<body>
	<div class="container">
		<div class="col-lg-12">
			<h1>Filter text example</h1>
			<hr>
			<form class="form" action="index.jsp">
				<div class="form-group">
					<p class="help-block">Type in your text with html code in the form below
					<div class="input-group">
						<span class="input-group-addon">&lt;&nbsp;/&gt;</span> 
						<input type="text" class="form-control" name="input" id="input" placeholder="eg. &lt;b&gt;Bold text">
					</div><!-- input-group -->
				</div><!-- /form-group -->
				<button class="btn btn-primary" type="submit">Absenden</button>
			</form>
			<hr>
		</div><!-- /col-lg-12 -->
		<div class="col-lg-12">
			<p class="help-block">
				See your result below after you submitted some text
				<div class="jumbotron">
				<blockquote>${ param.input }</blockquote>
			</div><!-- /jumbotron -->
		</div>
	</div>
</body>
</html>