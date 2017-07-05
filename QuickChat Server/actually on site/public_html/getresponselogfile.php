<?php
	
	require "../scripts/globals.php";
	
	//http://php.net/manual/en/function.readfile.php
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
			header('Content-Description: File Transfer');
			header('Content-Type: application/octet-stream');
			header('Content-Disposition: attachment; filename=' . basename($responseLogFile));
			header('Expires: 0');
			header('Cache-Control: must-revalidate');
			header('Pragma: public');
			header('Content-Length: ' . filesize($responseLogFile));
			
			//http://php.net/manual/en/function.ob-end-flush.php
			while (ob_get_level() > 0)
			{
				ob_end_flush();
			}
			
			readfile($responseLogFile);
		}
	}
	
?>