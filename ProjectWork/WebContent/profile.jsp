<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Spielerprofil</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.1/css/bootstrap.min.css">
<!-- custom css file -->
<link rel="stylesheet" href="style.css" />
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
		
	</div>

	<div class="container">
		<div class="text-center">
			<c:if test="${not empty deleteButton}">
    				${deleteButton}
			</c:if>
		</div>
		
		<div class="row">
			<div class="panel panel-default">
				<div class="panel-heading">
					<h4>Spielerprofil von ${first_name} ${last_name}</h4>
				</div>
				<c:if test="${not empty success}">
    				${success}
				</c:if>
				
				<div class="panel-body">
					<div class="col-md-4">
					
							<h2>Informationen</h2>
							
								<table class="table table-borderless table-responsive">
									<tr>
										<td class="col-md-2">Spieler-ID:</td>
										<td class="col-md-2"><b>${player_id}</b></td>
									</tr>
									<tr>
										<td>Geburtstag:</td>
										<td><b>${birthday }</b></td>
									</tr>
									<tr>
										<td>Alter:</td>
										<td><b>${age}</b></td>
									</tr>
									<tr>
										<td>Geschlecht:</td>
										<td><b>${gender}</b></td>
									</tr>
									<tr>
										<td>Aktueller Verein:</td>
										<td><b>${current_club}</b></td>
									</tr>
									<tr>
										<td>Liga:</td>
										<td><b>${league}</b></td>
									</tr>
									<tr>
										<td>Bevorzugte Position:</td>
										<td><b>${preferred_position}</b></td>
									</tr>
									
								
								</table>
	
					
						
					</div>
					<div class="col-md-4">
						<img src="https://x1.xingassets.com/assets/frontend_minified/img/users/nobody_m.original.jpg"
							id="profile-image1" class="img-responsive">
					</div>
					<div class="col-md-4">
						<h2>Attribute</h2>
							
								<table class="table table-borderless table-responsive">
								
									<tr>
										<td>Offensive:</td>
										<td><b>${offensive}</b></td>
									</tr>
									<tr>
										<td>Defensive:</td>
										<td><b>${defensive}</b></td>
									</tr>
									<tr>
										<td>Zuspiele:</td>
										<td><b>${passing}</b></td>
									</tr>
									<tr>
										<td>Fairplay:</td>
										<td><b>${fairplay}</b></td>
									</tr>
									<tr>
										<td>Vitalitaet:</td>
										<td><b>${vitality}</b></td>
									</tr>
									<tr>
										<td>Zweikampf:</td>
										<td><b>${duels}</b></td>
									</tr>
								
								</table>
						
					</div>
					
				</div>
			</div>
		</div>
	</div>
	
	<div class="container fixed-ticker-entries pre-scrollable">
		<table class="table">
			<c:forEach items='${ticker_entries}' var='ticker_entry'>
		
				<tr>
					<td>${ticker_entry}</td>
				</tr>
	
			</c:forEach>
	
			
		</table>
	</div>
	

</body>
</html>