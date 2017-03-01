import java.io.File;
import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PImage;

// Runner is in charge of resource management
// and communicating with Neural Pupppet

public class Runner {
	public PApplet p;
	public Animation a;
	
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
		if(true) // set to false for debugging
		{
			//Tom's pipeline directory
			set_input_folder("N:\\pipeline\\inputs");
			set_watch_folder("N:\\pipeline\\enhanced3");
			// MacOS Desktop
			//set_input_folder(System.getProperty("user.home") + "/Desktop/_input");
			//set_input_folder(System.getProperty("user.home") + "/Desktop/_watch"); 
		}
		else
		{
			// Gets the directory where your program started 
			// http://stackoverflow.com/questions/17540942/how-to-get-the-path-of-running-java-program#answer-17541023
			String working_directory = new File(".").getAbsolutePath();
			working_directory = working_directory.substring(0,working_directory.length()-1);
			
			set_input_folder(working_directory + "_input");
			set_watch_folder(working_directory + "_watch");
			
			empty_folder(input_folder);
			empty_folder(watch_folder);
		}
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
		check_folder_exists(input_folder);
	}
	
	public void set_watch_folder(String watch_directory)
	{
		watch_folder = new File(watch_directory);
		check_folder_exists(watch_folder);
	}
	
	public File input_folder()
	{
		return input_folder;
	}
	
	public File watch_folder()
	{
		return watch_folder;
	}
	
	// Folder Operations
	public void check_folder_exists(File folder)
	{
		if(!folder.exists()) {folder.mkdir();}
	}
	
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
		
		public Watch(File file, String callback, Object object)
		{
			this.file = file;
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
					//String file_path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."));
					//File watched_file = new File(file_path + ".png");
					
					//System.out.println("Watcher watching for " + watched_file.getAbsolutePath());
					
					File watched_file = check_for_file(file);
					
					if(watched_file != null && file_is_fine(watched_file))
					{
						System.out.println("Watcher found file! " + file.getAbsolutePath());
						run_callback(watched_file);
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
		
		public File check_for_file(File file)
		{
			File checked_file = check_for_file_format(file, ".png");
			if(checked_file != null) {p.println("found .png!"); return checked_file;}
			
			checked_file = check_for_file_format(file, ".jpg");
			if(checked_file != null) {p.println("found .jpg!"); return checked_file;}

			checked_file = check_for_file_format(file, ".gif");
			if(checked_file != null) {p.println("found .gif!"); return checked_file;}
			
			checked_file = check_for_file_format(file, ".tga");
			if(checked_file != null) {p.println("found .tga!"); return checked_file;}
			
			return null;
		}
		
		public File check_for_file_format(File file, String format)
		{
			String file_path = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."));
			File check_file = new File(file_path + format);
			
			p.println("Watching for: " + check_file.getAbsolutePath());
			
			if(check_file.exists())
			{
				p.println("Found it!");
				return check_file;
			}
			else
			{
				return null;
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
		
		public void run_callback(File found_file)
		{
			try
			{
				System.out.println(object);
				method.invoke(object, found_file);
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
