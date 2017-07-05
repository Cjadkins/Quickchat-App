<?php
	require "../scripts/globals.php";
	
	verifyLoggedIn();
	//"users/username/location.txt"
	$locationFileName = "$usersFolder/" . $_SESSION["username"] . "/$locationFile";
	
	if (file_exists($locationFileName))
	{
		//Open the current user's location file.
		$file = new CSVFile($locationFileName, true, "r", $newLine);
		
		//Go to the end of the file.
		$file->seekEnd();
		
		//Read the most recent location.
		$lastSegment = $file->readPreviousSegment();
		
		//Close the current user's location file.
		$file->closeFile();
		
		//Make sure the current user has a location.
		if (empty($lastSegment))
		{
			//Current user's location is unknown.
			returnMessage("5240100010", "Location is unknown!");
		}
		
		//Split the last segment to get the latitude and longitude.
		$lastSegment = str_getcsv($lastSegment, ",");
		
		//Store the latitude and longitude.
		$latitude = $lastSegment[0];
		$longitude = $lastSegment[1];
		
		
		//For storing the users and their distances.
		$usersList = array();
		$usersToSort = 0;
		
		//Open the usernames file.
		$file = new CSVFile($usernamesFile, true, "r", $newLine);
		
		//Go through all of the usernames in the usernames file.
		do
		{
			//Read the next segment in the usernames file.
			$otherUsername = $file->readNextSegment();
			
			//If the segment read is empty, stop reading.
			if (empty($otherUsername))
			{
				break;
			}
			
			//Split the segment to get the username.
			$otherUsername = str_getcsv($otherUsername, ",")[0];
			
			//Skip the current user's username.
			if ($otherUsername == $_SESSION["username"])
			{
				continue;
			}
			
			//"users/otherUsername/location.txt"
			$locationFileName = "$usersFolder/$otherUsername/$locationFile";
			
			if (file_exists($locationFileName))
			{
				//Open the other user's location file.
				$lFile = new CSVFile($locationFileName, true, "r", $newLine);
				
				//Go to the end of the file.
				$lFile->seekEnd();
				
				//Read the most recent location.
				$lastSegment = $lFile->readPreviousSegment();
				
				//Close the other user's location file.
				$lFile->closeFile();
				
				//Make sure the other user has a location.
				if (empty($lastSegment))
				{
					//Other user's location is unknown.
					continue;
				}
				
				//Split the last segment to get the latitude and longitude.
				$lastSegment = str_getcsv($lastSegment, ",");
				
				//Store the latitude and longitude.
				$otherLatitude = $lastSegment[0];
				$otherLongitude = $lastSegment[1];
				
				
				//Calculate the distance between the current user's location and the other user's location.
				$distance = calculateDistance($latitude, $longitude, $otherLatitude, $otherLongitude);
				
				//Add the other user's username and their distance to the users list.
				$usersList[$usersToSort] = array($otherUsername, $distance);
				++$usersToSort;
			}
			else
			{
				//Location file doesn't exist.
				returnMessage("5240000020", "Server data error!");
			}
		}
		while (!$file->stopReading);
		
		//Close the usernames file.
		$file->closeFile();
		
		
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
					
					//Distances.
					$firstDistance = $firstUser[1];
					$secondDistance = $secondUser[1];
					
					//Check for swap.
					if ($firstDistance > $secondDistance)
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
		returnMessage("0240201030", $returnString);
	}
	else
	{
		//Location file doesn't exist.
		returnMessage("5240000040", "Server data error!");
	}

?>
