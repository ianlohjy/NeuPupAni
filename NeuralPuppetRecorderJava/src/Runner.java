import java.io.File;
import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PImage;

// Runner is in charge of resource management
// and communicating with Neural Pupppet

public class Runner {
	public PApplet p;
	public Animation a;

	// Folders
	String working_directory = "";
	String input_directory = "";
	String watch_directory = "";
	
	String input_folder_name = "_input";
	String watch_folder_name = "_watch";
	File input_folder;
	File watch_folder; 
	
	// Watch
	Watch watcher;
	
	public Runner(PApplet p, Animation a)
	{
		this.p = p;
		this.a = a;
	}
	
	public void setup()
	{
		setup_directories();
	}
	
	public void run()
	{
		if(watcher != null)
		{
			watcher.watch_out();
		}
	}
	
	// Watch
	public void stop_watch()
	{
		watcher = null;
	}
	
	public void start_watch(File file_to_watch, String callback, Object object)
	{
		stop_watch();
		watcher = new Watch(file_to_watch, callback, object);
	}
	
	// Directory setup
	public void set_input_folder(String input_directory)
	{
		input_folder = new File(input_directory);
	}
	
	public void set_watch_folder(String watch_directory)
	{
		watch_folder = new File(watch_directory);
	}
	
	public void setup_directories()
	{
		// Gets the directory where your program started 
		// http://stackoverflow.com/questions/17540942/how-to-get-the-path-of-running-java-program#answer-17541023
		working_directory = new File(".").getAbsolutePath();
		working_directory = working_directory.substring(0,working_directory.length()-1);
		System.out.println("Running from directory: " + working_directory);
		
		// Checking if folders exist
		// Setting directories
		input_directory = working_directory + input_folder_name;
		watch_directory = working_directory + watch_folder_name;
		
		// Temporary override to Tom's pipeline directory
		input_directory = "N:\\pipeline\\inputs";
		watch_directory = "N:\\pipeline\\enhanced";
		
		input_folder = new File(input_directory);
		watch_folder = new File(watch_directory);
		
		if(!input_folder.exists()) {input_folder.mkdir();}
		if(!watch_folder.exists()) {watch_folder.mkdir();}
		
		//empty_folder(input_folder);
		//empty_folder(watch_folder);
	}
	
	// Folder Operations
	public void empty_folder(File folder)
	{
		String[] inside_folder = folder.list();
		for(String file: inside_folder)
		{
			File file_to_delete = new File(folder.getAbsolutePath() + File.separator + file);
			System.out.println("Deleting file " + file_to_delete.getAbsolutePath());
			file_to_delete.delete();
		}
	}
	
	public class Watch
	{
		File file;
		String callback;
		Object object;
		Method method;
		
		int watch_start;
		int last_watch;
		int watch_interval = 1000;
		int watch_timeout = 30000;
		
		/*
		public Watch(File file, String callback, Object object)
		{
			
		}
		*/
		
		public Watch(File file, String callback, Object object)
		{
			// Override file watching to .png file
			String original_file = file.getAbsolutePath();
			String expected_file = original_file.substring(0, original_file.lastIndexOf('.')) + ".png"; 
			
			this.file = file;
			this.file = new File(expected_file);
			watch_start = p.millis();
			last_watch = 0;
			this.object = object;
			this.method = set_callback(method, object, callback);
			watch_out();
		}
		
		public void watch_out()
		{
			if((p.millis() - watch_start) < watch_timeout)
			{
				if((p.millis() - last_watch) > 1000)
				{
					System.out.println("Watcher watching for " + file.getAbsolutePath());
					
					if(file.exists() && file_is_fine(file))
					{
						System.out.println("Watcher found file! " + file.getAbsolutePath());
						run_callback();
						Runner.this.stop_watch();
					}
					last_watch = p.millis();
				}
			}
			else
			{
				System.out.println("Watcher timed out!");
				Runner.this.stop_watch();
			}
		}
		
		public boolean file_is_fine(File file)
		{
			try
			{
				PImage check_image = p.loadImage(file.getAbsolutePath());
				return true;
			}
			catch(Exception e)
			{
				p.println("Found watch file is not valid");
				e.printStackTrace();
				return false;
			}
		}
		
		public void run_callback()
		{
			try
			{
				System.out.println(object);
				method.invoke(object, file);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public Method set_callback(Method method, Object object, String method_name)
		{
			try
			{
				Class[] args = new Class[1];
				args[0] = File.class;
				method = object.getClass().getDeclaredMethod(method_name, args);
				return method;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}
