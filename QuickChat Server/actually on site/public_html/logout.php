<?php
	
	require "../scripts/globals.php";
	
	//Destroy session on server.
	session_unset();
	session_destroy();
	session_write_close();
	
	//Delete cookie.
	setcookie(session_name(), "", 0, "/");
	
	//Logged out successfully.
	returnMessage("0300100010", "You are now logged out.");
	
?>