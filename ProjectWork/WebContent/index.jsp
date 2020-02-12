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

<div id="nav" class="navigation">
	<a href="index.jsp">Spielersuche</a>
  	<a href="nlp.jsp">NLP</a>
</div>
<body>
	<div class="jumbotron text-center">
  		<h1>Parametrisierbare Suchanfrage</h1>
	</div>
	<div class="container">
		<div class="col-lg-12">
			<form class="form-horizontal" role="form" action="QueryServlet" method="post">
				<p class="help-block">Waehlen Sie die gewuenschten Eigenschaften des Spielers aus:</p>
				<div class = "form-row">
					<div class = "form-group col-md-6">
						<label for = "alter">Alter</label>
      					<select class="form-control" size="1" id="alter" name="alter">
        					<option value ="1">1</option>
        					<option value ="2">2</option>
        					<option value ="3">3</option>
						</select>
					</div>
					
					<div class = "form-group col-md-6">
						<label for = "geschlecht">Geschlecht</label>
      					<select class="form-control" size="1" id="geschlecht" name="geschlecht">
        					<option value ="1">maennlich</option>
        					<option value ="2">weiblich</option>
        					<option value ="3">divers</option>
						</select>
					</div>
  				 </div>
  				 
  				 <div class = "form-row">
					<div class = "form-group col-md-6">
						<label for = "spielklasse">Spielklasse / Liga</label>
      						<select class="form-control" size="1" name="spielklasse">
        						<option value ="1">1. Bundesliga</option>
        						<option value ="2">2. Bundesliga</option>
        						<option value ="3">3. Bundesliga</option>
        						<option value ="4">Regionalliga</option>
							</select>
					</div>
					
					<div class = "form-group col-md-6">
						<label for = "bevourzugte_position">Bevorzugte Position</label>
      					<select class="form-control" size="1" id="bevorzugte_position" name="bevorzugte_position">
        					<option value ="1">Torwart</option>
        					<option value ="2">Verteidigung</option>
        					<option value ="3">Mittelfeld</option>
        					<option value ="4">Sturm</option>
						</select>
					</div>
  				 </div>
  				 
  				 <div class="range-field w-25">
  				 
  				 	<label for = "offensive">Offensive</label>
  				 	<input class="border-0" type="range" min="0" max="100" />
  				 
  				 	<label for = "defensive">Defensive</label>
  				 	<input class="border-0" type="range" min="0" max="100" />
  				 	
  				 	<label for = "zuspiele">Zuspiele</label>
  				 	<input class="border-0" type="range" min="0" max="100" />
  				 	
  					 <label for = "vitalitaet">Vitalitaet</label>
  				 	<input class="border-0" type="range" min="0" max="100" />
  				 	
  				 	<label for = "fairness">Fairness</label>
  				 	<input class="border-0" type="range" min="0" max="100" />
  				 	
  				 	<label for = "zweikampf">Zweikampf</label>
  				 	<input class="border-0" type="range" min="0" max="100" />
  				 
  				 </div>
  				 
      				
				
				<button class="btn btn-primary" type="submit">Suche!</button>
			</form>
		</div>
	</div>
</body>
</html>