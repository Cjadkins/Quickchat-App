<?php
	
	require "../scripts/globals.php";
	
	if (file_exists($chatroomsFile))
	{
		$file = new CSVFile($chatroomsFile, true, "r", $newLine);
		
		$line = $file->readNextSegment();
		
		while (!$file->stopReading)
		{
			echo $line . "<br/>";
			
			$line = $file->readNextSegment();
		}
		
		/*if (file_exists($accessLogFile))
		{
			readfile($accessLogFile);
		}*/
	}
	
?>