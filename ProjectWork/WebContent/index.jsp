<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spielersuche</title>
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
  		<h2>Parametrisierbare Suchanfrage fuer Fussballspieler</h2>
  		<p>Spezifizieren Sie Parameter, um passende Spieler zu finden! <br/>
  		Waehlen Sie die Expertenansicht fuer eine erweiterte Suche!</p>
	</div>
	
	<!-- Width is always 12 -->
	<div class="form-group col-md-8 col-md-offset-3">
		<form role="form" action="QueryServlet" method="post">
			
			<div class="row">
				<label class ="col-md-2" for = "age">Alter</label>
				<label class ="col-md-2" for = "gender">Geschlecht</label>
				<label class ="col-md-2" for = "league">Spielklasse / Liga</label>
				<label class ="col-md-2" for = "preferred_position">Bevorzugte Position</label>
			</div>
			
			<div class="row">
				<div class="col-md-2">
				<select class="form-control col-md-2" size="1" id="age" name="age">
        			<% for( int i=1; i<=50; i++) { %>
						<option value="<%=i %>"><%= i %></option>
					<% } %>
        					
				</select>
				</div>
				<div class="col-md-2">
				<select class="form-control" size="1" id="gender" name="gender">
        			<option value ="male">maennlich</option>
        			<option value ="female">weiblich</option>
        			<option value ="diverse">divers</option>
				</select>
				</div>
				<div class="col-md-2">
      			<select class="form-control" size="1" name="league">
        			<option value ="1. Bundesliga">1. Bundesliga</option>
        			<option value ="2. Bundesliga">2. Bundesliga</option>
        			<option value ="3. Bundesliga">3. Bundesliga</option>
        			<option value ="Regionalliga">Regionalliga</option>
				</select>
				</div>
				<div class="col-md-2">
      			<select class="form-control col-md-2" size="1" id="preferred_position" name="preferred_position">
        			<option value ="goalkeeper">Torwart</option>
        			<option value ="defense">Verteidigung</option>
        			<option value ="midfield">Mittelfeld</option>
        			<option value ="storm">Sturm</option>
				</select>
				</div>
			</div>
			<br>
			
  			<div class="container">
  				<button class="btn btn-danger" type="reset">Zuruecksetzen!</button>
				<button class="btn btn-success" type="submit">Suche!</button>
  			</div>
		</form>
	</div>
	
	<div>
	</div>

</body>
</html>