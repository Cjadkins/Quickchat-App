<?php
	
	require "../scripts/globals.php";
	
	//http://php.net/manual/en/function.readfile.php
	if (file_exists($accessLogFile))
	{
		$fsize = filesize($accessLogFile);
		
		if ($fsize > 5000)
		{
			rename($accessLogFile, "$logsFolder/accessLog backup " . date("F j - Y - g;i a") . ".txt");
			
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
			header('Content-Disposition: attachment; filename=' . basename($accessLogFile));
			header('Expires: 0');
			header('Cache-Control: must-revalidate');
			header('Pragma: public');
			header('Content-Length: ' . filesize($accessLogFile));
			
			//http://php.net/manual/en/function.ob-end-flush.php
			while (ob_get_level() > 0)
			{
				ob_end_flush();
			}
			
			readfile($accessLogFile);
		}
	}
	
?>