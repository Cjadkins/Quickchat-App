<?php
	
	require "../scripts/globals.php";
	require "../scripts/CSVFile.php";
	
	verifyLoggedIn();
	
	$chatroomHashID = requireVariable("chatroomHashID");
	$message = requireVariable("message");
	
	//"chatrooms/chatroomHashID.txt"
	$chatroomFileName = "$chatroomsFolder/$chatroomHashID.txt";
	
	//Verify that the chatroom file exists.
	if (file_exists($chatroomFileName))
	{
		//Open the chatroom file.
		$file = new CSVFile($chatroomFileName, true, "a+", $chatroomDelimiter);
		
		//Go to the end of the file and check for a break.
		$noMessages = $file->seekEndWithBreak();
		
		//Default to message ID 0.
		$nextMessageID = 0;
		
		//Check to see if there are any chat messages.
		if (!$noMessages)
		{
			//Read the second to last line (chat message's ID).
			$file->readPreviousSegment();
			$secondToLastLine = $file->readPreviousSegment();
			
			//Increment by 1 to get the next chat message ID.
			$nextMessageID = $secondToLastLine + 1;
		}
		
		//Append the new chat message and its data to the end of the file.
		$file->appendOrError($_SESSION["username"] . "$chatroomDelimiter$message$chatroomDelimiter$nextMessageID$chatroomDelimiter" . time() . "$chatroomDelimiter");
		
		//Now go to the beginning of the file.
		$file->seekBeginning();
		
		//Read the two usernames at the beginning of the file.
		$username1 = $file->readNextSegment();
		$username2 = $file->readNextSegment();
		
		//Close the file.
		$file->closeFile();
		
		touch("$usersFolder/$username1/$previousUsersFile");
		touch("$usersFolder/$username2/$previousUsersFile");
		
		returnMessage("0360100010", "Message sent.");
	}
	else
	{
		//The chatroom file does not exist.
		returnMessage("5360000020", "Invalid parameters!");
	}
	
?>