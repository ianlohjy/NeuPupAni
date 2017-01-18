
import processing.core.*;
import processing.event.MouseEvent;

import java.lang.reflect.*;

public class MethodReflection extends PApplet{

	Gui gui;
	Gui.Button test_button;
	
	public static void main(String args[])
	{
		PApplet.main(new String[] { "MethodReflection" });
	}
	
	public void settings()
	{
		size(500,500);
	}
	
	public void setup()
	{
		gui = new Gui(this);
		test_button = gui.button();
		
		test_button.x(50).y(50).width(100).height(25).label("FUCK");
		test_button.up_colour(150, 150, 150, 255);
		test_button.over_colour(100, 100, 100, 255);
		test_button.down_colour(50, 50, 50, 255);
		test_button.on_down_function("testMethod", this);
	}
	
	public void draw()
	{
		//run("testMethod");
		gui.draw();
	}
	
	public void run(String function)
	{
		Method method;
		
		try 
		{
			method = this.getClass().getMethod(function);
			
			try
			{
				method.invoke(this);
			}
			catch(Exception e)
			{
			}
		} 
		catch(Exception e)
		{
		}
	}
	
	public void mouse_event(MouseEvent e)
	{
		gui.mouse_event(e);
		//println(e.getAction());
	}
	
	public void mouseClicked(MouseEvent e)
	{	
		// 3
		//println("CLICKED");
		mouse_event(e);
	}
	
	public void mouseDragged(MouseEvent e)
	{
		// 4
		//println("DRAGGED");
		mouse_event(e);
	}
	
	public void mouseMoved(MouseEvent e)
	{
		// 5
		//println("MOVED");
		mouse_event(e);
	}
	
	public void mousePressed(MouseEvent e)
	{
		// 1
		///println("PRESSED");
		mouse_event(e);
	}
	
	public void mouseReleased(MouseEvent e)
	{
		// 2
		//println("RELEASED");
		mouse_event(e);
	}
	
	public void testMethod()
	{
		println("hi!!!");
	}
	
}
