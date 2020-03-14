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
  		<h2>Fuege neue Spieler anhand eines fussball.de Profils hinzu!</h2>
	</div>
	
	<div class="container text-center">
		<div class="form-group">
			<form role="form" action="ProfileToFormServlet" method="post">
				<div class="row">
					<div class="col">
						<input name="url_profile" class="form-control text-center" type="url" placeholder="URL des zu analysierenden fussball.de Profils">
					</div>
				</div>
				<br/>
				<div class="row text-center">

					<button class="btn btn-success" type="submit">Profil verarbeiten!</button>

				</div>



			</form>
		</div>
	</div>

	<div class="container form-group text-center">
		<form role="form" action="CreateCaseServlet" method="post">
			<div class="row col-md-12">
				
					<label for="first_name">Vorname:<input required name="first_name" class="form-control" type="text" placeholder= "z.B. 'Max'" value= ${firstName }></label>
					<label for="last_name">Nachname:<input required name="last_name" class="form-control" type="text" placeholder= "z.B. 'Mustermann'"value= ${lastName }></label>
					<label for="gender">Geschlecht:<input required name="gender" class="form-control" type="text" placeholder= "z.B. 'maennlich'" value= ${gender }></label>
					<label for="birthday">Geburtstag:<input required name="birthday" class="form-control" type="text" placeholder= "z.B. '01.01.2000'" value= ${birthday }></label>
					<label for="current_club">Aktueller Verein:<input name="current_club" class="form-control" placeholder= "z.B. 'FC Musterstadt'" type="text" value= ${currentClub }></label>
					
				
			</div>
			<div class="row col-md-12">
			
					<label for="league">Spielklasse:<input required name="league" class="form-control" type="text" placeholder= "z.B. '1. Bundesliga'"></label>	
					<label for="preferred_position">Bevorzugte Position:<input required name="preferred_position" class="form-control" type="text" placeholder= "z.B. 'Mittelfeld'"></label>	
					
				
			</div>

			<div class="row">
				<button class="btn btn-danger" type="reset">Zuruecksetzen!</button>
				<button class="btn btn-success" type="submit">Spieler anlegen!</button>
  			</div>
				
		</form>
	</div>

	
	<div class="container text-center">
		<c:if test="${not empty success}">
    		${success}
		</c:if>
	</div>
</body>
</html>