<?php
	
	require "../scripts/globals.php";
	require "../scripts/CSVFile.php";
	
	verifyLoggedIn();
	
	$timestamp = tryGetVariable("timestamp", 0);
	
	//"users/username/previousUsers.txt"
	$previousUsersFileName = "$usersFolder/" . $_SESSION["username"] . "/$previousUsersFile";
	
	if (file_exists($previousUsersFileName))
	{
		//Check to see if there are any previous users chatted with since the latest update.
		if (filemtime($previousUsersFileName) >= $timestamp)
		{
			//There has been a more recent chat.
			
			//For storing the users and their distances.
			$usersList = array();
			$usersToSort = 0;
			
			//Open the previous users file.
			$file = new CSVFile($previousUsersFileName, true, "r", $newLine);
			
			//Go through all of the lines in the previous users file.
			do
			{
				//Read the next line in the previous users file.
				$nextLine = $file->readNextSegment();
				
				//If the line read is empty, stop reading.
				if (empty($nextLine))
				{
					break;
				}
				
				$nextLine = str_getcsv($nextLine, ",");
				
				$usersList[$usersToSort] = array($nextLine[0], $nextLine[1]);
				++$usersToSort;
			}
			while (!$file->stopReading);
			
			//Close the previous users file.
			$file->closeFile();
			
			
			//Fetch all of the filemtimes and replace the chatroomHashID in each inner array with the filemtime.
			for ($userNum = 0; $userNum < $usersToSort; ++$userNum)
			{
				//"chatrooms/chatroomHashID.txt"
				$usersList[$userNum][1] = filemtime($chatroomsFolder . "/" . $usersList[$userNum][1] . ".txt");
			}
			
			
			//Don't sort if there aren't more than 1 users.
			if ($usersToSort > 1)
			{
				//Go through each iteration of sorting.
				for ($x = 1; $usersToSort - $x > 0; ++$x)
				{
					//Go through each pair of swap checks.
					for ($currentSwapNum = 0; $currentSwapNum < $usersToSort - $x; ++$currentSwapNum)
					{
						$firstUser = $usersList[$currentSwapNum];
						$secondUser = $usersList[$currentSwapNum + 1];
						
						//Timestamps.
						$firstTimestamp = $firstUser[1];
						$secondTimestamp = $secondUser[1];
						
						//Check for swap.
						if ($firstTimestamp < $secondTimestamp)
						{
							//Swap.
							$usersList[$currentSwapNum] = $secondUser;
							$usersList[$currentSwapNum + 1] = $firstUser;
						}
					}
				}
			}
			
			
			//0th username.
			$returnString = $usersList[0][0];
			
			for ($userNum = 1; $userNum < $usersToSort; ++$userNum)
			{
				//Usernames.
				$returnString .= "," . $usersList[$userNum][0];
			}
			
			//Return the list of sorted usernames.
			returnMessage("0260201010", $returnString);
		}
		else
		{
			//There are no previous users chatted with since the latest update.
			returnMessage("0260100020", "No previous users to update.");
		}
	}
	else
	{
		//Previous users file doesn't exist.
		returnMessage("5260000030", "Server data error!");
	}
	
?>