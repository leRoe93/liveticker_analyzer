<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spielerprofil</title>
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
  <h2>Spielerprofil von: ${first_name} ${last_name}</h2>
</div>

<div class="container">

	<c:if test="${not empty success}">
    	${success}
	</c:if>
	
	<c:if test="${not empty deleteButton}">
    	${deleteButton}
	</c:if>

	<div class="row">
	
		<div class="col-sm-4">
			
            <h6>Spieler ID: ${player_id}</h6>
            <h6>Alter: ${age}</h6>
            <h6>Geschlecht: ${gender}</h6>
            <h6>Aktueller Verein: ${current_club}</h6>
            <h6>Liga: ${league}</h6>
            <h6>Bevorzugte Position: ${preferred_position}</h6>
			
        </div>
		
        <div class="col-sm-4">
        	
		    <img src="Pictures/profilbild.png" class="img-circle">
        	
        </div>
        
        <div class="col-sm-4">
        	
        	<h6>Offensive: ${offensive}</h6>
            <h6>Defensive: ${defensive}</h6>
            <h6>Zuspiele: ${passes}</h6>
            <h6>Fairplay: ${fairplay}</h6>
            <h6>Vitalitaet: ${vitality}</h6>
            <h6>Zweikampf: ${duels}</h6>
        
        </div>
	</div>
	<br>
	
	<div class="container fixed-ticker-entries pre-scrollable">
		<table>
			<c:forEach items='${ticker_entries}' var='ticker_entry'>
		
				<tr>
					<td>${ticker_entry}</td>
				</tr>
	
			</c:forEach>
	
			
		</table>
	</div>

	
</div>
	
	
	
	
</body>
</html>