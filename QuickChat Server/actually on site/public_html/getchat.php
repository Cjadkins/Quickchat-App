<?php
	
	require "../scripts/globals.php";
	require "../scripts/CSVFile.php";
	
	verifyLoggedIn();
	
	$chatroomHashID = requireVariable("chatroomHashID");
	$timestamp = tryGetVariable("timestamp", 0);
	
	//"chatrooms/chatroomHashID.txt"
	$chatroomFileName = "$chatroomsFolder/$chatroomHashID.txt";
	
	//Verify that the chatroom file exists.
	if (file_exists($chatroomFileName))
	{
		//Check to see if there are any messages since the latest update.
		if (filemtime($chatroomFileName) >= $timestamp) 
		{
			//Open the chatroom file.
			$file = new CSVFile($chatroomFileName, true, "r", $chatroomDelimiter);
			
			//Go to the end of the file.
			$file->seekEnd();
			
			//Start with an empty message list.
			$messageList = "";
			
			//Read as many newer messages as needed or until there is an error.
			while (!$file->stopReading)
			{
				//Read each message timestamp.
				$messageTime = $file->readPreviousSegmentWithBreak();
				
				//Verify that the beginning of the messages hasn't been reached and
				//that this message is newer than the latest update timestamp.
				if (!empty($messageTime) && $messageTime >= $timestamp)
				{
					//Read the message ID, the message itself, and the username that sent it.
					$messageID = $file->readPreviousSegmentWithBreak();
					$message = $file->readPreviousSegmentWithBreak();
					$messageUser = $file->readPreviousSegmentWithBreak();
					
					//Verify that the data is not corrupt.
					if ((empty($messageID) && $messageID !== "0") || empty($message) || empty($messageUser))
					{
						if (empty($messageList))
						{
							//No chat messages have been sent at all.
							returnMessage("0200000010", "No chat messages have been sent.");
						}
						else
						{
							//For some reason, the message data is corrupt.
							returnMessage("5200000020", "Server data error!");
						}
					}
					else
					{
						//This message is newer than the latest update timestamp and it was read
						//successfully, so prepend it to the message list being returned.
						$messageList = "$messageUser$chatroomDelimiter$message$chatroomDelimiter$messageID$chatroomDelimiter$messageTime$chatroomDelimiter$chatroomDelimiter$messageList";
					}
				}
				else
				{
					break;
				}
			}
			
			//Close the file.
			$file->closeFile();
			
			//Return the list of newer messages.
			returnMessage("0200204030", $messageList);
		}
		else
		{
			//There are no newer messages than the latest update.
			returnMessage("5200000040", "No new messages.");
		}
	}
	else
	{
		//The chatroom file does not exist.
		returnMessage("5200000050", "Invalid parameters!");
	}
	
?>