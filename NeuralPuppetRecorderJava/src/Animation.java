import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.file.Files;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Animation {

	NeuralPuppetRecorder p;
	Runner r;
	
	// Playback
	int framerate = 25;
	
	int playing = 0;
	int stopped = 1;
	int recording = 2;
	int playback = stopped;
	int last_playback_time;
	
	int current_frame;

	// Animation Data
	AnimData recorded_data;
	
	// Interpolation
	int[] face_grid_shape = {1,1};
	
	// Images
	PImage face_input;
	PImage face_grid;
	
	Animation(NeuralPuppetRecorder p, Runner r)
	{
		this.p = p;
		this.r = r;
		recorded_data = new AnimData(p);
		last_playback_time = p.millis();
	}
	
	void update()
	{
		handle_playback(p.mouseX, p.mouseY);
	}
	
	void handle_playback(float rx, float ry)
	{
		if(recorded_data.points != null)
		{
			if(playback == recording || playback == playback)
			{
				// If enough time has passed for a new frame
				if((p.millis() - last_playback_time) > (1000/framerate)) 
				{
					if(playback == recording)
					{
						recorded_data.add_point(rx, ry);
						last_playback_time = p.millis();
						current_frame = recorded_data.size()-1;
						//System.out.println("RECORDING");
					}
					else if(playback == playing)
					{
						current_frame++;
						if(current_frame > recorded_data.size()-1)
						{
							current_frame = 0;
						}
						last_playback_time = p.millis();
					}
				}
			}
		}
	}
	
	void play()
	{
		if(playback != recording && recorded_data.points != null)
		{
			playback = playing;
			p.play_button_play();
		}
		else
		{
			p.play_button_stop();
		}
	}
	
	void stop()
	{
		if(playback != recording)
		{
			current_frame = 0;
			pause();
			p.play_button_stop();
		}
	}
	
	void pause()
	{
		if(playback != recording)
		{
			playback = stopped;
			p.play_button_stop();
		}
	}
	
	// Recording
	void start_recording(float rx, float ry)
	{
		// If this is the first time recording is started
		if(playback != recording)
		{
			recorded_data.clear_points();
			playback = recording;
			last_playback_time = 0;
			
			p.play_button_stop();
		}
		handle_playback(rx, ry);
	}
	
	void stop_recording()
	{
		playback = stopped;
	}
	
	void loop_recording()
	{
		if(recorded_data != null && recorded_data.points != null)
		{
			if(recorded_data.size() > 3)
			{
				float distance_between_ends = p.dist(recorded_data.get_point(0).x, recorded_data.get_point(0).y, 
													 recorded_data.get_point(recorded_data.size()-1).x, recorded_data.get_point(recorded_data.size()-1).y);
			
				if(distance_between_ends > 5)
				{
					recorded_data.points.addAll(recorded_data.get_loop(recorded_data.points));
				}
			}
		}
	}
	
	// Saving & Loading
	public void select_save_path()
	{
		p.selectOutput("Saving file", "save", null, this);
	}
	
	public void select_load_path()
	{
		p.selectInput("Load a json file", "load", null, this);
	}
	
	public void save(File save_path)
	{
		if(save_path != null)
		{
			recorded_data.save_to_json(save_path);
		}
	}
	
	public void load(File load_path)
	{
		if(load_path != null)
		{
			recorded_data.load_json(load_path);
		}
	}
	
	// Image IO  handling
	public void get_face()
	{
		p.selectInput("Select an image to animate", "face_selected", null, this);
	}
	
	public void get_grid()
	{
		p.selectInput("Select an grid to load", "grid_done_processing", null, this);
	}
	
	public void face_selected(File selection)
	{
		//String file_path = selection.getAbsolutePath();
		String file_format = selection.toString().substring(selection.toString().lastIndexOf('.'), selection.toString().length()).toLowerCase();
		//String file_wo_format = file_path.substring(0, file_path.lastIndexOf('.'));
				
		if(file_format.equals(".gif") || file_format.equals(".jpg") || file_format.equals(".png") || file_format.equals(".tga"))
		{
			System.out.println("Found file compatible file format: " + file_format);
			
			// Load Face image as PImage
			face_input = p.loadImage(selection.getAbsolutePath());
			
			String mac_address = "";
			try
			{
				InetAddress address = InetAddress.getLocalHost();
				NetworkInterface nwi = NetworkInterface.getByInetAddress(address);
				byte mac[] = nwi.getHardwareAddress();
				mac_address = mac.toString();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			String file_name = selection.getName();
			file_name = file_name.substring(0, file_name.lastIndexOf("."));
			
			/*
			String working_filename = Integer.toString(p.year())   +
									  Integer.toString(p.month())  +
									  Integer.toString(p.day())    +
									  Integer.toString(p.minute()) +
								      Integer.toString(p.millis()) + 
								      mac_address;
			*/
			String working_filename = file_name + "_" + Long.toString(System.currentTimeMillis());// + mac_address;
			
			File copy_file = new File(r.input_folder().getAbsolutePath() + File.separator + working_filename + file_format);
			File result_file = new File(r.watch_folder().getAbsolutePath() + File.separator + working_filename + file_format);
			
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
		try
		{
			face_grid = p.loadImage(file.getAbsolutePath());
			face_grid_shape[0] = 7;
			face_grid_shape[1] = 7;
		}
		catch(Exception e)
		{
			p.println("Encountered error while loading image grid");
			e.printStackTrace();
		}
	}
	
	public PVector get_current_playback_position()
	{
		if(playback == recording || playback == playing)
		{
			if(recorded_data.points != null)
			{
				return recorded_data.get_point(current_frame);
			}
			else
			{
				return new PVector(p.mouseX, p.mouseY);
			}
		}
		else
		{
			return new PVector(p.mouseX, p.mouseY);
		}
	}
	
	public int[] get_face_grid_shape()
	{
		return face_grid_shape;
	}
	
	public PImage get_face_grid()
	{
		return face_grid;
	}
}
