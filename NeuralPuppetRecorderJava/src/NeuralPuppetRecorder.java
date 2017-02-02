import processing.core.*;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

public class NeuralPuppetRecorder extends PApplet{
	
	Canvas canvas;
	Display display; 
	Animation animation;
	Runner runner;
	
	// GUI
	Gui gui;
	// GUI Menu Bar
	Gui.Button save_json;
	Gui.Button load_json;
	Gui.Button load_face;
	Gui.Button render;
	// GUI Timeline
	Gui.Button play;
	
	boolean show_framerate = false;
	
	public static void main(String args[])
	{
		PApplet.main(new String[] { "NeuralPuppetRecorder" });
	}
	
	// Processing methods
	public void settings()
	{
		size(1000, 550, P3D);
	}
	
	public void setup()
	{
		frameRate(120);
		//surface.setResizable(true);	
		runner = new Runner(this, animation);
		animation = new Animation(this, runner);
		canvas = new Canvas(this, animation);
		display = new Display(this, animation);
		
		setup_gui();
		runner.setup();
		ortho();
	}
	
	public void draw()
	{	
		background(255);
		check_window_size();
		
		canvas.draw();
		display.draw();
		animation.update();
		
		runner.run();
		
		update_gui();
		
		if(show_framerate)
		{
			pushStyle();
			fill(0);
			textAlign(RIGHT, CENTER);
			text((int)frameRate + " FPS", display.x+display.w-5, display.y+10);
			popStyle();
		}
	}
	
	public void check_window_size()
	{	// UNUSED
		// Make sure that window:
		// - height is (width/2)+(25)
		
		int min_width  = 500;
		int new_width  = width;
		int new_height = height;
	}
	
	// Processing events
	public void mouse_event(MouseEvent e) 
	{
		gui.mouse_event(e);
		canvas.mouse_event(e);
		display.mouse_event(e);
	}
	
	public void mouseClicked(MouseEvent e)  {mouse_event(e);}
	public void mouseDragged(MouseEvent e)  {mouse_event(e);}
	public void mouseMoved(MouseEvent e)    {mouse_event(e);}
	public void mousePressed(MouseEvent e)  {mouse_event(e);}
	public void mouseReleased(MouseEvent e) {mouse_event(e);}
	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == 70) // F key
		{
			if(show_framerate)
			{
				show_framerate = false;
			}
			else
			{
				show_framerate = true;
			}
		}
	}
	
	// GUI
	public void setup_gui()
	{
		gui = new Gui(this);
		
		load_json = gui.button();
		save_json = gui.button();
		load_face = gui.button();
		play = gui.button();
		render = gui.button();
		
		load_json.height(25).label("LOAD JSON");
		load_json.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		load_json.on_down_function("select_load_path", animation);
		
		save_json.height(25).label("SAVE JSON");
		save_json.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		save_json.on_down_function("select_save_path", animation);
		
		load_face.height(25).label("LOAD FACE");
		load_face.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		load_face.on_down_function("get_face", animation);
		
		render.height(25).label("RENDER");
		render.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		
		play.height(25).label("PLAY").set_toggle();
		play.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		play.on_down_function("play", animation).on_up_function("stop", animation);
	}
	
	public void play_button_play()
	{
		play.on();
		play.down_label("PLAYING").over_label("STOP");
	}
	
	public void play_button_stop()
	{
		play.off();
		play.up_label("PLAY");
	}
	
	public void update_gui()
	{
		int top_division = (int)width/4;
		load_json.x(0).y(0).width(top_division);
		save_json.x(top_division*1).y(0).width(top_division);
		load_face.x(top_division*2).y(0).width(top_division);
		render.x(top_division*3).y(0).width(top_division);
		
		int bottom_division = (int)width/4;
		play.x(0).y(25+(int)canvas.h).width(bottom_division);
		
		gui.draw();
		
		if(animation.recorded_data != null && animation.recorded_data.points != null)
		{
			float timeline_width = (width-play.w) * (animation.current_frame+1) / (float)animation.recorded_data.size();

			pushStyle();
			fill(255,0,0);
			noStroke();
			rectMode(CORNER);
			rect(play.x+play.w, play.y, timeline_width, play.h);
			popStyle();
		}
	}
}
