<?php
	
	//UTF-8
	header('Content-Type: text/html; charset=utf-8');
	
	//Make sure session hasn't already been started.
	if (!strlen(session_id()))
	{
		//Start the session.
		session_start();
	}
	
	
	$newLine = "\n";
	$chatroomDelimiter = "\x7C";
	
	$sleepRandMin = 10;
	$sleepRandMax = 50;
	$hashingAlgorithm = "sha512";
	$saltPrepend = "furious";
	$saltAppend = "bananas!";
	
	
	//In root folder.
	$usersFolder = $_SERVER["DOCUMENT_ROOT"] . "/../users";
	$chatroomsFolder = $_SERVER["DOCUMENT_ROOT"] . "/../chatrooms";
	$logsFolder = $_SERVER["DOCUMENT_ROOT"] . "/../logs";
	$usernamesFile = $_SERVER["DOCUMENT_ROOT"] . "/../usernames.txt";
	$chatroomsFile = $_SERVER["DOCUMENT_ROOT"] . "/../chatrooms.txt";
	$accessLogFile = $_SERVER["DOCUMENT_ROOT"] . "/../accessLogFile.txt";
	$responseLogFile = $_SERVER["DOCUMENT_ROOT"] . "/../responseLogFile.txt";
	
	//Inside "$usersFolder/username/".
	$passwordFile = "password.txt";
	$previousUsersFile = "previousUsers.txt";
	$locationFile = "location.txt";
	
	
	function verifyLoggedIn()
	{
		fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Verifying logged in.<br/>\n");
		
		if (!isset($_SESSION["loggedin"], $_SESSION["username"]) || !$_SESSION["loggedin"] === true)
		{
			//User is not logged in.
			returnMessage("5120000010", "You are not logged in!");
		}
	}
	
	
	//Checks HTTP POST for specified string and returns value of passed in variable.
	//Errors if variable is unsupplied or empty.
	function requireVariable($variableString)
	{
		fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Requiring variable { \"$variableString\" }.<br/>\n");
		
		//Check to see if the variable was passed in and make sure it's not empty.
		if (isset($_POST[$variableString]) && !empty($_POST[$variableString]))
		{
			fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Variable { \"$variableString\" } has value { " . $_POST[$variableString] . " }.<br/>\n");
			
			//Return the HTTP POST variable's value.
			return $_POST[$variableString];
		}
		else
		{
			//Given parameters are invalid or empty.
			returnMessage("5120000020", "Invalid parameters!");
		}
	}
	
	//Checks HTTP POST for specified string and returns value of passed in variable.
	//Allows empty variable values. Errors if variable is unsupplied.
	function requireVariableEmpty($variableString)
	{
		fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Requiring non-empty variable { \"$variableString\" }.<br/>\n");
		
		//Check to see if the variable was passed in and make sure it's not empty.
		if (isset($_POST[$variableString]))
		{
			fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Variable { \"$variableString\" } has value { " . $_POST[$variableString] . " }.<br/>\n");
			
			//Return the HTTP POST variable's value.
			return $_POST[$variableString];
		}
		else
		{
			//Given parameters are invalid.
			returnMessage("5120000030", "Invalid parameters!");
		}
	}
	
	//Checks HTTP POST for specified string and returns value of passed in variable or $defaultValue.
	function tryGetVariable($variableString, $defaultValue)
	{
		fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Checking for optional variable { \"$variableString\" } with default value { \"$defaultValue\" }.<br/>\n");
		
		//Check to see if the variable was passed in and make sure it's not empty.
		if (isset($_POST[$variableString]) && !empty($_POST[$variableString]))
		{
			fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Variable { \"$variableString\" } has value { " . $_POST[$variableString] . " }.<br/>\n");
			
			//Return the HTTP POST variable's value.
			return $_POST[$variableString];
		}
		else
		{
			fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Variable { \"$variableString\" } has *default* value { \"$defaultValue\" }.<br/>\n");
			
			//Return the given $defaultValue.
			return $defaultValue;
		}
	}
	
	
	//Make sure the user has specified a valid username and password combination.
	function validateUsernameAndPassword($username, $password)
	{
		fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Validating username { \"$username\" } and password { \"$password\" }.<br/>\n");
		
		$usernameLength = strlen($username);
		$passwordLength = strlen($password);
		
		if ($usernameLength < 5)
		{
			fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Username { \"$username\" } too short!<br/>\n");
			
			returnMessage("5120100040", "Username is too short! Must be 5 or more characters.");
		}
		
		for ($i = 0; $i < $usernameLength; ++$i)
		{
			if ($username[$i] < '!' || $username[$i] > '~')
			{
				fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Username { \"$username\" } invalid! (a-z, A-Z, 0-9, or special characters)<br/>\n");
				
				returnMessage("5120100050", "Username is invalid! Must be a-z, A-Z, 0-9, or special characters.");
			}
		}
		
		if ($passwordLength < 5)
		{
			fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Password { \"$password\" } too short!<br/>\n");
			
			returnMessage("5120100060", "Password is too short! Must be 5 or more characters.");
		}
		
		for ($i = 0; $i < $passwordLength; ++$i)
		{
			if ($password[$i] < '!' || $password[$i] > '~')
			{
				fileAppendOrError($GLOBALS["accessLogFile"], time() . ": Password { \"$password\" } invalid! (a-z, A-Z, 0-9, or special characters)<br/>\n");
				
				returnMessage("5120100070", "Password is invalid! Must be a-z, A-Z, 0-9, or special characters.");
			}
		}
	}
	
	
	//Takes two usernames, a and b, sorts them alphabetically, and
	//pairs them together in a string with "&" in between.
	function pairUsernames($a, $b)
	{
		if ($a < $b)
		{
			return "$a&$b";
		}
		else
		{
			return "$b&$a";
		}
	}
	
	//Securely hashes the given string with a random microsecond sleep time.
	function uberSecureHashFunction($secureMe)
	{
		usleep(rand($GLOBALS["sleepRandMin"], $GLOBALS["sleepRandMax"]));
		
		return hash($GLOBALS["hashingAlgorithm"], $GLOBALS["saltPrepend"] . $secureMe . $GLOBALS["saltAppend"]);
	}
	
	
	//Creates and appends data to file or returns error code.
	function fileAppendOrError($fileName, $data)
	{
		//Append the given data to the end of the file.
		$result = file_put_contents($fileName, $data, FILE_APPEND);
		
		//Ensure that the function performed successfully.
		if ($result === FALSE)
		{
			returnMessage("5120000080", "Server could not perform operation.");
		}
	}
	
	function calculateDistance($lat1, $long1, $lat2, $long2)
	{
		$radLat1 = deg2rad($lat1);
		$radLat2 = deg2rad($lat2);
		
		return rad2deg(acos(sin($radLat1) * sin($radLat2) + cos($radLat1) * cos($radLat2) * cos(deg2rad($long1 - $long2)))) * 60.0 * 1.1507771462461513720116603376689;
	}
	
	
	//Outputs the given error/success code and message and stops execution.
	function returnMessage($code, $message)
	{
		fileAppendOrError($GLOBALS["responseLogFile"], time() . ": Returning code { \"$code\" } and message { \"$message\" }.<br/>\n");
		
		//Close the session.
		session_write_close();
		
		exit("$code: $message");
	}
	
?>