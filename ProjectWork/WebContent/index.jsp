<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spielersuche</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
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
		<h2>Parametrisierbare Suchanfrage fuer Fussballspieler</h2>
		<p>
			Spezifizieren Sie Parameter, um passende Spieler zu finden!<br/>
			Die Leistungsdaten sind von "unauffaellig" bis "auffaellig" einstellbar!
		</p>
	</div>

	<div class="container">
		<div class="row">
			<form role="form" action="QueryServlet" method="post">
				<div class="container row text-center bg-success">
					<h3><b>Persoenliche Informationen</b></h3>
				</div>
				<div class="row text-center bg-success">
					<label class="col-md-3" for="age">Alter</label> <label
						class="col-md-3" for="gender">Geschlecht</label> <label
						class="col-md-3" for="league">Spielklasse / Liga</label> <label
						class="col-md-3" for="preferred_position">Position
						</label>
				</div>
				
				
			
				<div class="container row bg-success">
					<div class="col-md-3">
						<select class="form-control col-md-2" size="1" id="age" name="age">
							<% for( int i=1; i<=50; i++) { %>
							<option value="<%=i %>"><%= i %></option>
							<% } %>

						</select>
					</div>
					<div class="col-md-3">
						<select class="form-control" size="1" id="gender" name="gender">
							<option value="male">maennlich</option>
							<option value="female">weiblich</option>
							<option value="diverse">divers</option>
						</select>
					</div>
					<div class="col-md-3">
						<select class="form-control" size="1" name="league">
							<option value="1. Bundesliga">1. Bundesliga</option>
							<option value="2. Bundesliga">2. Bundesliga</option>
							<option value="3. Bundesliga">3. Bundesliga</option>
							<option value="Regionalliga">Regionalliga</option>
						</select>
					</div>
					<div class="col-md-3">
						<select class="form-control col-md-2" size="1"
							id="preferred_position" name="preferred_position">
							<option value="goalkeeper">Torwart</option>
							<option value="defense">Verteidigung</option>
							<option value="midfield">Mittelfeld</option>
							<option value="storm">Sturm</option>
						</select>
					</div>
				</div>
				
				<div class="container row text-center bg-info">
					<h3><b>Leistungsdaten</b></h3>
				</div>
				
				
				<div class="container row text-center bg-info">
					<label class="col-md-2" for="offensive">Offensive</label> 
					<label class="col-md-2" for="defensive">Defensive</label>
					<label class="col-md-2" for="fairplay">Fairplay</label> 
					<label class="col-md-2" for="passing">Zuspiele</label>
					<label class="col-md-2" for="duels">Zweikampf</label>
					<label class="col-md-2" for="vitality">Vitalitaet</label>
				</div>
				<div class="container row bg-info">
					<div class="col-md-2">
						<input type="range" class="custom-range" value="1" min="1" max="10" step="1" name="offensive" id="offensive">
					</div>
					<div class="col-md-2">
						<input type="range" class="custom-range" value="1" min="1" max="10" step="1" name="defensive" id="defensive">
					</div>
					<div class="col-md-2">
						<input type="range" class="custom-range" value="1" min="1" max="10" step="1" name="fairplay" id="fairplay">
						
					</div>
					<div class="col-md-2">
						<input type="range" class="custom-range" value="1" min="1" max="10" step="1" name="passing" id="passing">
					</div>
					<div class="col-md-2">
						<input type="range" class="custom-range" value="1" min="1" max="10" step="1" name="duels" id="duels">
					</div>
					<div class="col-md-2">
						<input type="range" class="custom-range" value="1" min="1" max="10" step="1" name="vitality" id="vitality">
					</div>
				</div>
				
				<br><br>
				<div class="container row">
					<div class="col-md-3"></div>
					<div class="col-md-3">
						<button class="btn col-md-12 btn-danger" type="reset">Zuruecksetzen!</button>
					</div>
					<div class="col-md-3">
						<button class="btn col-md-12 btn-success" type="submit">Suche!</button>
					</div>
					<div class="col-md-3"></div>
				</div>

			</form>
		</div>

	</div>



</body>
</html>