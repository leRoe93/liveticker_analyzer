<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NLP</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>

<nav id="nav" class="navbar navbar-inverse navbar-fixed-top">
	<div class="container-fluid">
		<ul class="nav navbar-nav">
      		<li><a href="index.jsp">Spielersuche</a></li>
      		<li><a href="nlp.jsp">NLP</a></li>
      		<li><a href="addPlayer.jsp">Spieler hinzufügen</a></li>
    	</ul>

  	</div>
</nav>
<body>

	<div class="jumbotron text-center">
  		<h1>Input der Live-Ticker fuer NLP</h1>
	</div>

	<div class="form-group col-md-8 col-md-offset-3">
		<form role="form" action="LiveTickerProcessorServlet" method="post">
			<div class="row">
				<div class="col-md-8">
					<input name="url_lt" class="form-control" type="text" placeholder="URL des zu analysierenden Live-Tickers">
				</div>
			</div>
			<br>

			<div class="row">
				<button class="btn btn-primary" type="submit">Auf geht's!</button>
  			</div>
				
		</form>
	</div>

</body>
</html>