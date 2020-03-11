<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spieler hinzufügen</title>
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
  		<h1>Füge neue Spieler anhand eines fussball.de Profils hinzu!</h1>
	</div>

	<div class="form-group col-md-8 col-md-offset-3">
		<form role="form" action="ProfileToFormServlet" method="post">
			<div class="row">
				<div class="col-md-8">
					<input name="url_profile" class="form-control" type="text" placeholder="URL des zu analysierenden fussball.de Profils">
				</div>
			</div>
			<br>

			<div class="row">
				<button class="btn btn-primary" type="submit">Profil verarbeiten!</button>
  			</div>
				
		</form>
	</div>
	
	<div class="form-group col-md-8 col-md-offset-3">
		<form role="form" action="CreateCaseServlet" method="post">
			<div class="row">
				<div class="col-md-8">
					<label for="first_name">Vorname:<input name="first_name" class="form-control" type="text" placeholder= "z.B. 'Max'" value= ${firstName }></label>
					<label for="last_name">Nachname<input name="last_name" class="form-control" type="text" placeholder= "z.B. 'Mustermann'"value= ${lastName }></label>
					<label for="gender">Geschlecht:<input name="gender" class="form-control" type="text" placeholder= "z.B. 'maennlich'" value= ${gender }></label>
					<label for="birthday">Geburtstag:<input name="birthday" class="form-control" type="text" placeholder= "z.B. '01.01.2000'" value= ${birthday }></label>
					<label for="current_club">Aktueller Verein:<input name="current_club" class="form-control" placeholder= "z.B. 'FC Musterstadt'" type="text" value= ${currentClub }></label>
					<label for="league">Spielklasse:<input name="league" class="form-control" type="text" placeholder= "z.B. '1. Bundesliga'"></label>	
					<label for="preferred_position">Bevorzugte Position:<input name="preferred_position" class="form-control" type="text" placeholder= "z.B. 'Mittelfeld'"></label>	
					
				</div>
			</div>
			<br>

			<div class="row">
				<button class="btn btn-primary" type="submit">Spieler anlegen!</button>
  			</div>
				
		</form>
	</div>
	
	<div class="container">
		<c:if test="${not empty success}">
    		${success}
		</c:if>
	</div>
</body>
</html>