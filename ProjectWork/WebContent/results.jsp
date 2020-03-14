<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Resultate</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<!-- custom css file -->
<link rel="stylesheet" href="style.css" />
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
  		<h2>Gefundene Spieler mit absteigender Ähnlichkeit</h2>
	</div>
	<div class="container text-center">
		<h3>Folgende Gewichtungen wurden zugrundegelegt:</h3>
		<p>Alter: ${age }, Geschlecht: ${gender }, Spielklasse: ${league }, Position: ${preferred_position }, Offensive: ${offensive }, Defensive: ${defensive }, Zuspiele: ${passing }, Fairplay: ${fairplay }, Zweikaempfe: ${duels }, Vitalitaet: ${vitality }</p>
		<c:if test="${not empty results}">
    		${results}
		</c:if>
	</div>
</body>
</html>