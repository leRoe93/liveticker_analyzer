<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<c:set var="AppBase" value="${pageContext.request.contextPath}"></c:set>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Progression</title>
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
  		<h2>Resultate aus der Live-Ticker-Analyse</h2>

		<p>
		--> Spalte 1: Direkt uebernommene Eintraege aus Live-Ticker <br/>
		--> Spalte 2: Erkannte Entitaeten in Zusammenhang mit einem Nomen oder einem Verb stellen potenzielle Aktionen dar<br/>
		--> Spalte 3: Gefilterte potenzielle Aktionen durch Abgleich der Entitaeten mit der Spielerdatenbank<br/>
		--> Spalte 4: Attribut, welches sich durch die erkannte Situation bei dem erkannten Spieler veraendert<br/>
		</p>
				
		<p>Die Resultate wurden aus <a href='${ticker_url}'>DIESEM</a> Live-Ticker abgeleitet.</p>
	</div>
	
	<div class="container">
		<c:if test="${not empty tickerEntries}">
    		${tickerEntries}
		</c:if>
	</div>

</body>
</html>