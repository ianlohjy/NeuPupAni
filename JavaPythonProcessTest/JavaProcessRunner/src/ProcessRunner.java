import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.*;

/*
 * This is a test program to see how processing plays with runtime.processes (in this case, it is a python script)
 */

public class ProcessRunner extends PApplet{

	static String working_directory = "";
	ExternalProcess ext_process;
	
	public static void main(String args[])
	{
		PApplet.main(new String[] { "ProcessRunner" });
		// Gets the directory where your program started 
		// http://stackoverflow.com/questions/17540942/how-to-get-the-path-of-running-java-program#answer-17541023
		working_directory = new File(".").getAbsolutePath();
		working_directory = working_directory.substring(0,working_directory.length()-1);
	}
	
	public void settings()
	{
		size(250,250);
	}
	
	public void setup()
	{
		ext_process = new ExternalProcess(this);
		ext_process.runFor((int)random(10));
	}
	
	public void draw()
	{
		//background(0);
		
		if(!ext_process.isRunning())
		{
			ArrayList<String> lines = ext_process.readStream();
			
			for(String line : lines)
			{
				println("Process output: " + line);
			}
		}
		
		if(!ext_process.isRunning())
		{
			ext_process.runFor((int)random(10));
		}
		
		
		fill(255);
		noStroke();
		ellipse( map( sin(radians(frameCount)), -1, 1, 15, width-15), map(cos(radians(frameCount)/2), -1,1, 15, height-15), 15, 15);
		fill(0, 15);
		rect(0, 0, width, height);
	}
	
	class ExternalProcess
	{
		String script_file;
		String command;
		
		Runtime runtime;
		Process process;
		
		InputStream input_stream;
		InputStreamReader input_stream_reader;
	    BufferedReader buffered_reader;
		
	    Method callback_method;
	    
	    PApplet p;
	    
		ExternalProcess(PApplet p)
		{
			script_file = working_directory + "scripts\\test_program.py";
			command = "python " + script_file;
			runtime = Runtime.getRuntime();
			
			this.p = p;
		}
		
		void runFor(int seconds)
		{
			if(!isRunning())
			{
				try 
				{
					println("Process started");
					
					process = runtime.exec(command + " " + Integer.toString(seconds));

					input_stream = process.getInputStream();
					input_stream_reader = new InputStreamReader(input_stream);
				    buffered_reader = new BufferedReader(input_stream_reader);
				    
				    /*
				    String line = null;
				    
				    while((line = buffered_reader.readLine()) != null)
				    {
				    	println(line);
				    }
				    */
				} 
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				println("Process is already running!");
			}
		}
		
		
		
		ArrayList<String> readStream()
		{
			try 
			{
				String line;
				ArrayList<String> lines = new ArrayList<String>();

				while((line = buffered_reader.readLine()) != null)
				{
					lines.add(line);
				}
				
				return lines;
			} 
			catch (IOException e) 
			{
				println("Could not read stream");
				e.printStackTrace();
			}
				
			return null;
		}
		
		boolean isRunning()
		{
			if(process == null)
			{
				return false;
			}
			else
			{
				return process.isAlive();
			}	
		}
		
		/*
		boolean hasExited()
		{
			if(process == null)
			{
				return false;
			}
			else
			{
				try
				{
					int exit_value = process.exitValue();
					println("Process finished with value of " + exit_value);
					return true;
				}
				catch(Exception e)
				{
					println("Could not get process' exit value");
					return false;
				}
			}
		}
		*/
		
		void stop()
		{
			process.destroy();
		}
		
		/*// Trying callback implementation (http://stackoverflow.com/questions/160970/how-do-i-invoke-a-java-method-when-given-the-method-name-as-a-string)
		void setStreamCallback(String method_name)
		{
			try
			{
				callback_method = p.getClass().getMethod(method_name);
			}
			catch (Exception e)
			{
				println("Could not set callback!");
			}
		}
		
		void invokeCallback()
		{
			if(callback_method != null)
			{
				try
				{
					callback_method.invoke(p, null);
				}
				catch (Exception e)
				{
					println("Something went wrong with the callback, or it was not set!");
				}
			}
		}
		*/
	}
	
	
}

