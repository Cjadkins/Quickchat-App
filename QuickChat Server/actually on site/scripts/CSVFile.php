<?php
	
	class CSVFile
	{
		public $file, $delimiter, $stopReading;
		
		function __construct($fileName = NULL, $isFullPath = NULL, $fileMode = NULL, $passedDelimiter = NULL)
		{
			if (!isset($fileName) || empty($fileName) || !isset($isFullPath) || empty($isFullPath) ||
				!isset($fileMode) || empty($fileMode) || !isset($passedDelimiter) || empty($passedDelimiter))
			{
				returnMessage("5100000010", "Script error!");
			}
			
			$g = get_included_files();
			
			if ($isFullPath)
			{
				$this->file = fopen($fileName, $fileMode);
			}
			else
			{
				$this->file = fopen(dirname($g[1]) . "/" . $fileName, $fileMode);
			}
			
			$this->delimiter = $passedDelimiter;
			$this->stopReading = false;
		}
		
		//Set to read at the beginning of the file.
		function seekBeginning()
		{
			rewind($this->file);
			
			$this->stopReading = false;
		}
		
		//Read the next segment of the file (until there is an error, the beginning of the file
		//is reached, or any of the delimiting character are reached).
		function readNextSegment()
		{
			$str = "";
			
			if (!$this->stopReading)
			{
				//Read the first character.
				$nextCharacter = fgetc($this->file);
				
				//Check for EOF.
				if ($nextCharacter === FALSE)
				{
					$this->stopReading = true;
				}
				else
				{
					//Continue to append characters to the segment and read characters until there is an error,
					//the end of the file (EOF) is reached, or any of the delimiting character are reached.
					while ($nextCharacter !== FALSE && $nextCharacter !== $this->delimiter)
					{
						$str .= $nextCharacter;
						$nextCharacter = fgetc($this->file);
					}
					
					//Read and discard delimiting character until the first non-delimiting character is reached.
					while ($nextCharacter === $this->delimiter)
					{
						$nextCharacter = fgetc($this->file);
					}
					
					//Don't ask.
					if (fseek($this->file, -1, SEEK_CUR) == -1 || $nextCharacter === FALSE)
					{
						$this->stopReading = true;
					}
				}
			}
			
			return $str;
		}
		
		//Read the previous segment of the file (until there is an error, the beginning of the file
		//is reached, or any of the delimiting character are reached).
		function readPreviousSegment()
		{
			$str = "";
			
			if (!$this->stopReading)
			{
				//Read the last character.
				$nextCharacter = fgetc($this->file);
				
				//Check for EOF.
				if ($nextCharacter === FALSE)
				{
					$this->stopReading = true;
				}
				else
				{
					//Continue to prepend characters to the segment and read characters until there is an error,
					//the beginning of the file is reached, or any of the delimiting character are reached.
					while ($nextCharacter !== FALSE && $nextCharacter !== $this->delimiter)
					{
						$str = $nextCharacter . $str;
						
						if (ftell($this->file) < 2)
						{
							$this->stopReading = true;
							break;
						}
						
						fseek($this->file, -2, SEEK_CUR);
						$nextCharacter = fgetc($this->file);
					}
					
					//Read and discard delimiting character until the first non-delimiting character is reached.
					while (!$this->stopReading && $nextCharacter === $this->delimiter)
					{
						if (ftell($this->file) < 2)
						{
							$this->stopReading = true;
							break;
						}
						
						fseek($this->file, -2, SEEK_CUR);
						$nextCharacter = fgetc($this->file);
					}
					
					if (!$this->stopReading)
					{
						//Don't ask.
						fseek($this->file, -1, SEEK_CUR);
					}
				}
			}
			
			return $str;
		}
		
		//Read the previous segment of the file (until there is an error, the beginning of the file
		//is reached, or any of the delimiting character are reached). Breaks at the first delimiter.
		function readPreviousSegmentWithBreak()
		{
			$str = "";
			
			if (!$this->stopReading)
			{
				//Read the last character.
				$nextCharacter = fgetc($this->file);
				
				//Check for EOF.
				if ($nextCharacter === FALSE)
				{
					$this->stopReading = true;
				}
				else
				{
					//Continue to prepend characters to the segment and read characters until there is an error,
					//the beginning of the file is reached, or any of the delimiting character are reached.
					while ($nextCharacter !== FALSE && $nextCharacter !== $this->delimiter)
					{
						$str = $nextCharacter . $str;
						
						if (ftell($this->file) < 2)
						{
							$this->stopReading = true;
							break;
						}
						
						fseek($this->file, -2, SEEK_CUR);
						$nextCharacter = fgetc($this->file);
					}
					
					//Read and discard one delimiting character.
					if (!$this->stopReading && $nextCharacter === $this->delimiter)
					{
						if (ftell($this->file) < 2)
						{
							$this->stopReading = true;
						}
						else
						{
							fseek($this->file, -2, SEEK_CUR);
							$nextCharacter = fgetc($this->file);
						}
					}
					
					if (!$this->stopReading)
					{
						//Don't ask.
						fseek($this->file, -1, SEEK_CUR);
					}
				}
			}
			
			return $str;
		}
		
		//Set to read at the last character of the file that's not a delimiting character.
		function seekEnd()
		{
			fseek($this->file, -1, SEEK_END);
			
			$this->stopReading = false;
			
			//Read the last character.
			$nextCharacter = fgetc($this->file);
			
			//Read and discard delimiting character until the first non-delimiting character is reached.
			while (!$this->stopReading && $nextCharacter === $this->delimiter)
			{
				if (ftell($this->file) < 2)
				{
					$this->stopReading = true;
					break;
				}
				
				fseek($this->file, -2, SEEK_CUR);
				$nextCharacter = fgetc($this->file);
			}
			
			if (!$this->stopReading)
			{
				//Don't ask.
				fseek($this->file, -1, SEEK_CUR);
			}
		}
		
		//Set to read at the last character of the file (before the ending delimiter) and return whether there is another delimiter.
		function seekEndWithBreak()
		{
			fseek($this->file, -1, SEEK_END);
			
			$this->stopReading = false;
			
			//Read the last character.
			$nextCharacter = fgetc($this->file);
			
			//Read and discard last delimiting character, if any.
			if ($nextCharacter === $this->delimiter)
			{
				if (ftell($this->file) < 2)
				{
					$this->stopReading = true;
					break;
				}
				
				fseek($this->file, -2, SEEK_CUR);
				$nextCharacter = fgetc($this->file);
			}
			
			if (!$this->stopReading)
			{
				//Don't ask.
				fseek($this->file, -1, SEEK_CUR);
			}
			
			//Check for two delimiting characters in a row at the end of the file.
			if ($nextCharacter === $this->delimiter)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		//Appends data to the end of the file or returns error code.
		function appendOrError($data)
		{
			$result = fwrite($this->file, $data);
			
			//Ensure that the function performed successfully.
			if ($result === FALSE)
			{
				returnMessage("5100000090", "Server could not perform operation.");
			}
		}
		
		function closeFile()
		{
			fclose($this->file);
			
			$this->stopReading = true;
		}
	}
	
?>