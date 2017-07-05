<?php
	
	require "../scripts/globals.php";
	
	if (file_exists($responseLogFile))
	{
		$fsize = filesize($responseLogFile);
		
		if ($fsize > 5000)
		{
			rename($responseLogFile, "$logsFolder/accessLog backup " . date("F j - Y - g;i a") . ".txt");
			
			die("moved response log to: $logsFolder/accessLog backup " . date("F j - Y - g;i a") . ".txt");
		}
		else if ($fsize === false)
		{
			die("bad file size - shouldn't happen...");
		}
		else
		{
			readfile($responseLogFile);
		}
	}
	
?>