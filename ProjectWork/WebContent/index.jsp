<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spielersuche</title>
</head>
<link rel="stylesheet" href="style.css">

<div id="nav" class="navigation">
	<a href="index.jsp">Spielersuche</a>
  	<a href="nlp.jsp">NLP</a>
</div>
<body>
	<div class="container">
		<div class="col-lg-12">
			<h1>Parametrisierbares Suchformular für Fussballspieler in der CBR-Datenbank</h1>
			<hr>
			<form class="form" action="QueryServlet" method="post">
				<div class="form-group">
					<p class="help-block">Waehlen Sie die gewuenschten Eigenschaften des Spielers aus:
					<div class="input-group">
						<table id="suche">
							<tr>
								<td>Alter:</td>
								<td>
									
									<input type="number" class="form-control" name="alter" id="alter" min="1" max="100" value="1">	
								</td>
								<td>Offensive:</td>
								<td>
									<input type="range" min="1" max="100" value="50" id="offensive">
								</td>
								
							</tr>
							
							<tr>
								<td>Geschlecht:</td>
								
								<td>
									
									<select size="1" id="geschlecht" name="geschlecht">
        								<option value ="1">maennlich</option>
        								<option value ="2">weiblich</option>
        								<option value ="3">divers</option>
									</select>
								</td>
								<td>Defensive:</td>
								<td>
									<input type="range" min="1" max="100" value="50" id="defensive">
								</td>
								
							</tr>
							<tr>
								<td>Spielklasse / Liga:</td>
								<td>
									<select size="1" name="spielklasse">
        								<option value ="1">1. Bundesliga</option>
        								<option value ="2">2. Bundesliga</option>
        								<option value ="3">3. Bundesliga</option>
        								<option value ="4">Regionalliga</option>
									</select>
								</td>
								<td>Vitalitaet:</td>
								
								<td>
									<input type="range" min="1" max="100" value="50" id="vitalitaet">
								</td>
								
							</tr>
							<tr>
								<td>Bevorzugte Position:</td>
								<td>
									
									<select size="1" name="bevorzugte_position">
        								<option value ="1">Torwart</option>
        								<option value ="2">Verteidigung</option>
        								<option value ="3">Mittelfeld</option>
        								<option value ="4">Sturm</option>
									</select>
								</td>
								<td>Zweikampf:</td>
								<td>
									<input type="range" min="1" max="100" value="50" id="zweikampf">
								</td>
								
							</tr>
							<tr>
								<td></td>
								<td>
										
								</td>
								<td>Zuspiele:</td>
								<td>
									
									<input type="range" min="1" max="100" value="50" id="zuspiele">
								</td>
								
							</tr>

							<tr>
								<td></td>
								<td>
										
								</td>
								<td>Fairness:</td>
								<td>
									<input type="range" min="1" max="100" value="50" id="fairness">
								</td>
								
							</tr>
							
						</table>
					
					</div><!-- input-group -->
				</div><!-- /form-group -->
				<button class="btn btn-primary" type="submit">Suche!</button>
			</form>
			<hr>
		</div>
	</div>
</body>
</html>