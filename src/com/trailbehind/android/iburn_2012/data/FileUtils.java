package com.trailbehind.android.iburn_2012.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileUtils {
	
		// Efficiently convert a File object to a String
		public static String fileToString(File file) throws IOException{
			BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder file_content = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
			    file_content.append(line);
			}
			
			return file_content.toString();
		}

}
