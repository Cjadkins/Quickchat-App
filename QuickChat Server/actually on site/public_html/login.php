<?php
	
	require "../scripts/globals.php";
	require "../scripts/CSVFile.php";
	
	$username = requireVariable("username");
	$password = requireVariable("password");
	
	validateUsernameAndPassword($username, $password);
	
	//"users/username"
	$userDir = "$usersFolder/$username";
	
	//Check to see if the username already exists.
	if (file_exists($userDir))
	{
		//Open the password file.
		$file = new CSVFile("$userDir/$passwordFile", true, "r", $newLine);
		
		//Go to the end of the file.
		$file->seekEnd();
		
		$lastLine = str_getcsv($file->readPreviousSegment(), ",");
		
		//Close the file.
		$file->closeFile();
		
		if ($lastLine[0] == $password)
		{
			$_SESSION["username"] = $username;
			$_SESSION["loggedin"] = true;
			
			//Logged in successfully.
			returnMessage("0280100010", "You are now logged in.");
		}
		else
		{
			//Incorrect password.
			returnMessage("5280100020", "Incorrect password!");
		}
	}
	else
	{
		//Username does not exist.
		returnMessage("5280100030", "Username does not exist!");
	}
	
?>