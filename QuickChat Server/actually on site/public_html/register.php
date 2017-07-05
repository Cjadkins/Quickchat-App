<?php
	
	require "../scripts/globals.php";
	
	$username = requireVariable("username");
	$password = requireVariable("password");
	
	validateUsernameAndPassword($username, $password);
	
	//"users/username"
	$userDir = "$usersFolder/$username";
	
	//Check to see if the username already exists.
	if (file_exists($userDir))
	{
		//Username already exists.
		returnMessage("5340100010", "Username already exists!");
	}
	else
	{
		//Get timestamp.
		$time = time();
		
		//Create and append username and timestamp to "usernames.txt".
		fileAppendOrError($usernamesFile, "$username,$time$newLine");
		
		//Create "users/username" folder.
		mkdir($userDir, 0777);
		
		//Create and append password and timestamp to "users/username/password.txt".
		fileAppendOrError("$userDir/$passwordFile", "$password,$time$newLine");
		
		//Create "users/username/previousUsers.txt" and "users/username/location.txt" files.
		fileAppendOrError("$userDir/$previousUsersFile", "");
		fileAppendOrError("$userDir/$locationFile", "");
		
		//Successfully registered account.
		returnMessage("0340100020", "Account registered!");
	}
	
?>