<?php
	
	require "../scripts/globals.php";
	require "../scripts/CSVFile.php";
	
	verifyLoggedIn();
	
	$timestamp = tryGetVariable("timestamp", 0);
	
	//"users/username/location.txt"
	$locationFileName = "$usersFolder/" . $_SESSION["username"] . "/$locationFile";
	
	//Check to see if there is a more recent location since the latest update.
	if (filemtime($locationFileName) >= $timestamp) 
	{
		//Open the location file.
		$file = new CSVFile($locationFileName, true, "r", $newLine);
		
		//Go to the end of the file.
		$file->seekEnd();
		
		$lastLine = $file->readPreviousSegment();
		
		//Close the file.
		$file->closeFile();
		
		if (empty($lastLine))
		{
			//Return no location stored.
			returnMessage("5220100010", "No location stored!");
		}
		else
		{
			$lastLine = str_getcsv($lastLine, ",");
			
			//Return the updated location.
			returnMessage("0220202020", $lastLine[0] . "," . $lastLine[1]);
		}
	}
	else
	{
		//There is no more recent location than the latest update.
		returnMessage("0220100030", "No more recent location.");
	}
	
?>