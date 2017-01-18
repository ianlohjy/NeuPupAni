import processing.core.*;
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
	// GUI Timeline
	Gui.Button play;
	
	public static void main(String args[])
	{
		PApplet.main(new String[] { "NeuralPuppetRecorder" });
	}
	
	// Processing methods
	public void settings()
	{
		size(1000, 550);
	}
	
	public void setup()
	{
		//surface.setResizable(true);	
		runner = new Runner(this, animation);
		animation = new Animation(this, runner);
		canvas = new Canvas(this, animation);
		display = new Display(this, animation);
		
		setup_gui();
		runner.setup();
	}
	
	public void setup_gui()
	{
		gui = new Gui(this);
		
		load_json = gui.button();
		save_json = gui.button();
		load_face = gui.button();
		play = gui.button();
		
		load_json.height(25).label("LOAD JSON");
		load_json.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		
		save_json.height(25).label("SAVE JSON");
		save_json.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		
		load_face.height(25).label("LOAD FACE");
		load_face.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
		load_face.on_down_function("get_face", animation);
		
		play.height(25).label("PLAY");
		play.down_colour(0,0,0,255).up_colour(150,150,150,255).over_colour(50,50,50,255);
	}
	
	public void update_gui()
	{
		int top_division = (int)width/4;
		load_json.x(0).y(0).width(top_division);
		save_json.x(top_division*1).y(0).width(top_division);
		load_face.x(top_division*2).y(0).width(top_division);
		
		int bottom_division = (int)width/4;
		play.x(0).y(25+(int)canvas.h).width(bottom_division);
		
		gui.draw();
	}
	
	public void draw()
	{	
		check_window_size();
		
		canvas.draw();
		display.draw();
		update_gui();
		animation.update();
		
		runner.run();
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
	}
	
	public void mouseClicked(MouseEvent e)  {mouse_event(e);}
	public void mouseDragged(MouseEvent e)  {mouse_event(e);}
	public void mouseMoved(MouseEvent e)    {mouse_event(e);}
	public void mousePressed(MouseEvent e)  {mouse_event(e);}
	public void mouseReleased(MouseEvent e) {mouse_event(e);}
	
}
