import java.io.File;
import java.nio.file.CopyOption;
import java.nio.file.Files;

import javax.swing.text.html.MinimalHTMLWriter;

import processing.core.PApplet;
import processing.core.PImage;

public class Animation {

	PApplet p;
	Runner r;
	
	// Playback
	int framerate = 25;
	
	int playing = 0;
	int stopped = 1;
	int recording = 2;
	int playback = stopped;
	int last_record_time;
	
	int current_frame;

	// Animation Data
	AnimData recorded_data;
	
	PImage face_input;
	PImage face_grid;
	
	Animation(PApplet p, Runner r)
	{
		this.p = p;
		this.r = r;
		recorded_data = new AnimData(p);
		last_record_time = p.millis();
	}
	
	void update()
	{
		handle_recording(p.mouseX, p.mouseY);
	}
	
	void handle_recording(float rx, float ry)
	{
		if(playback == recording)
		{
			// If enough time has passed for a new frame
			if((p.millis() - last_record_time) > (1000/framerate)) 
			{
				recorded_data.addPoint(rx,  ry);
				last_record_time = p.millis();
				//System.out.println("RECORDING");
			}
		}
	}
	
	void start_recording(float rx, float ry)
	{
		// If this is the first time recording is started
		if(playback != recording)
		{
			recorded_data.clearPoints();
			playback = recording;
			last_record_time = 0;
		}
		handle_recording(rx, ry);
	}
	
	void stop_recording()
	{
		playback = stopped;
	}
	
	// Image IO  handling
	public void get_face()
	{
		System.out.println("HI");
		p.selectInput("Select a file to process:", "face_selected", null, this);
	}
	
	public void face_selected(File selection)
	{
		String file_format = selection.toString().substring(selection.toString().lastIndexOf('.'), selection.toString().length()).toLowerCase();
		
		if(file_format.equals(".gif") || file_format.equals(".jpg") || file_format.equals(".png") || file_format.equals(".tga"))
		{
			System.out.println("Found file compatible file format: " + file_format);
			
			String filename_to_process = p.millis() + file_format;
			File copy_file = new File(r.input_directory + File.separator + filename_to_process);
			File result_file = new File(r.watch_directory + File.separator + filename_to_process);
			
			System.out.println("Copying file as " + copy_file);
			
			try
			{
				Files.copy(selection.toPath(), copy_file.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				r.start_watch(result_file, "grid_done_processing", this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void grid_done_processing(File file)
	{
		System.out.println("Grid Processed: " + file.getAbsolutePath());
	}
}
