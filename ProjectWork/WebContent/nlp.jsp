<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NLP</title>
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
		<h2>Verarbeitung von Live-Tickern mittels NLP</h2>
		<p>
			Auf dieser Seite ist es moeglich, einen URL zu einem bestimmten
			Live-Ticker automatisch<br /> untersuchen und somit einzelne
			Spielaktionen mit den betroffenen Spielern zu erkennen.<br /> -->
			Momentan nur Live-Ticker von <a
				href="http://www.sportal.de/live-ticker/fussball-live/">sportal</a>
			unterstuetzt!
		</p>
	</div>


	<div class="container text-center">
		<div class="form-group">
			<form role="form" action="LiveTickerProcessorServlet" method="post">
				<div class="row">
					<div class="col">
						<input name="url_lt" class="form-control text-center" type="url"
							placeholder="URL des zu analysierenden Live-Tickers">
					</div>
				</div>
				<br>

				<div id="ballGif" class="text-center">
					<img src="https://media.giphy.com/media/WvuTFk2IN7jxoLVDkP/giphy.gif"></img>
					<p>Bitte warten, dies kann einige Minuten dauern...</p>
				</div>
				<div class="row">

					<button onclick="showGif()" class="btn btn-success" type="submit">
						<img src="https://image.flaticon.com/icons/svg/53/53283.svg" width="20" /> Auf geht's!
					</button>

				</div>



			</form>
		</div>
	</div>


</body>

<script>
function showGif() {
    document.getElementById("ballGif").style.display = "block";
}
</script>
</html>