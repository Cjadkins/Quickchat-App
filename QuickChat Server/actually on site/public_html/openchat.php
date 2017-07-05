<?php
	
	require "../scripts/globals.php";
	
	verifyLoggedIn();
	
	$otherUsername = requireVariable("otherUsername");
	
	if ($otherUsername == $_SESSION["username"])
	{
		returnMessage("5320100010", "You do not need QuickChat to chat with yourself!");
	}
	
	//"users/otherUsername"
	$otherUserDir = "$usersFolder/$otherUsername";
	
	//Check to see if the username already exists.
	if (file_exists($otherUserDir))
	{
		//Pair both usernames together and hash them to get the chatroomHashID.
		$chatroomHashID = uberSecureHashFunction(pairUsernames($_SESSION["username"], $otherUsername));
		
		if (file_exists("$chatroomsFolder/$chatroomHashID.txt"))
		{
			returnMessage("0320201020", $chatroomHashID);
		}
		else
		{
			//Create the chatroom file and add both usernames to the beginning.
			fileAppendOrError("$chatroomsFolder/$chatroomHashID.txt", $_SESSION["username"] . "$chatroomDelimiter$otherUsername$chatroomDelimiter$chatroomDelimiter");
			
			//Get timestamp.
			$time = time();
			
			//Add the new chatroom to the chatrooms file.
			fileAppendOrError($chatroomsFile, "$chatroomHashID.txt,$time$newLine");
			
			//"users/username"
			$userDir = "$usersFolder/" . $_SESSION["username"];
			
			//Add the other username, chatroomHashID, and timestamp to the current user's previous users file.
			fileAppendOrError("$userDir/$previousUsersFile", "$otherUsername,$chatroomHashID,$time$newLine");
			
			//Add the current username, chatroomHashID, and timestamp to the other user's previous users file.
			fileAppendOrError("$otherUserDir/$previousUsersFile", $_SESSION["username"] . ",$chatroomHashID,$time$newLine");
			
			//Return chatroomHashID.
			returnMessage("0320201030", $chatroomHashID);
		}
	}
	else
	{
		//Username does not exist.
		returnMessage("5320100040", "Username does not exist!");
	}
	
?>