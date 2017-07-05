<?php
	
	require "../scripts/globals.php";
	
	verifyLoggedIn();
	
	$latitude = requireVariable("latitude");
	$longitude = requireVariable("longitude");
	
	//Appends the given location to the end of the location file.
	fileAppendOrError("$usersFolder/" . $_SESSION["username"] . "/$locationFile", "$latitude,$longitude," . time() . $newLine);
	
	//Location set.
	returnMessage("0380100010", "Location set.");
	
?>