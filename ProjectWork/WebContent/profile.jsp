<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spielerprofil</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/js/bootstrap.min.js"></script>
</head>


<div class="navigation">
	<a href="index.jsp">Spielersuche</a>
  	<a href="nlp.jsp">NLP</a>
</div>


<body>


<div class="jumbotron text-center">
  <h1>Spielerprofil von: [Platzhalter]</h1>
</div>
	<h1>Spielerprofil von: ###PLATZHALTER###</h1>
	
	<img align="left" id="profilbild" src="Pictures/profilbild.png"/>
	
	<div class="profil_obere_mitte">
		<table>
			<tr>
				<td>Name:</td>
				<td></td>
			</tr>
			<tr>
				<td>Vorname:</td>
				<td></td>	
			</tr>
			<tr>
				<td>Alter:</td>
				<td></td>	
			</tr>
			<tr>
				<td>Geschlecht:</td>
				<td></td>	
			</tr>
			<tr>
				<td>Spielklasse / Liga:</td>
				<td></td>	
			</tr>
			<tr>
				<td>Bevorzugte Position:</td>
				<td></td>	
			</tr>
		</table>
	</div>
	
	<img align="right" id="wappen" src="Pictures/wappen.png"/>
	
	
	
</body>
</html>