import processing.core.*;
import processing.event.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.lang.reflect.*;

public class Gui {

	PApplet p;
	ArrayList<Element> elements;
	
	Gui(PApplet p)
	{
		this.p = p;
		elements = new ArrayList<Element>();
	}
	
	public void mouse_event(MouseEvent e)
	{
		for(Element element : elements)
		{
			element.mouse_event(e);
		}
	}
	
	public void draw()
	{
		for(Element element : elements)
		{
			element.draw();
		}
	}
	
	Button button()
	{
		Button test_button = new Button(p);
		elements.add(test_button);
		return test_button;
	}
	
	public class Button extends Element
	{
		int[] down_colour = {0,0,0,0}; // When button is activated
		int[] up_colour = {0,0,0,0}; // When button is not activated
		int[] over_colour = {0,0,0,0}; // When button is hovered
		int[] current_colour;
		
		int button_down = 1;
		int button_up = 0;
		int button_state = button_up;
		
		boolean button_toggle = false;
		
		Method on_down_function;
		Object on_down_object;
		Method on_up_function;
		Object on_up_object;
		
		boolean button_entered_down = false;
		
		String label = "";
		
		Button(PApplet p)
		{
			super(0, 0, 0, 0, p);
		}
		
		// Drawing
		@Override
		void draw()
		{
			if(mouse_state == mouse_over) {current_colour = over_colour;}
			else if(mouse_state == mouse_down) {current_colour = down_colour;}
			else 
			{ 
				if(button_state == button_down)
				{
					current_colour = down_colour;
				}
				else if(button_state == button_up)
				{
					current_colour = up_colour;
				}
			}
			
			p.pushStyle();
			p.noStroke();
			p.fill(current_colour[0], current_colour[1], current_colour[2], current_colour[3]);
			p.rectMode(p.CORNER);
			p.rect(x, y, w, h);
			
			p.fill(255, 255, 255);
			p.textAlign(p.CENTER, p.CENTER);
			p.text(label, x+(w/2), y+(h/2)); 
			
			p.popStyle();
		}
		
		// Setting attributes
		Button label(String label)
		{
			this.label = label;
			return this;
		}
		
		Button x(int x)
		{
			this.x = x;
			return this;
		}
		
		Button y(int y)
		{
			this.y = y;
			return this;
		}
		
		Button width(int width)
		{
			this.w = width;
			return this;
		}
		
		Button height(int height)
		{
			this.h = height;
			return this;
		}
		
		Button down_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			down_colour = new_colour;
			return this;
		}
		
		Button up_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			up_colour = new_colour;
			return this;
		}
		
		Button over_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			over_colour = new_colour;
			return this;
		}
		
		Button set_toggle()
		{
			button_toggle = true;
			return this;
		}
		
		Button set_moment()
		{
			button_toggle = false;
			return this;
		}
		
		// Behaviour
		Button on_down_function(String function, Object object)
		{
			on_down_object = object;
			on_down_function = set_function(on_down_function, object, function);
			return this;
		}
		
		Button on_up_function(String function, Object object)
		{
			on_up_object = object;
			on_up_function = set_function(on_up_function, object, function);
			return this;
		}
		
		@Override
		void pressed(boolean within_bounds)
		{
			super.pressed(within_bounds);
			
			if(within_bounds)
			{
				button_entered_down = true;
			}
		}
		
		@Override
		void released(boolean within_bounds)
		{
			super.released(within_bounds);
			
			if(within_bounds && button_entered_down)
			{
				if(button_toggle)
				{
					if(button_state == button_down)
					{
						run_function(on_up_function, on_up_object);
						button_state = button_up;
					}
					else if(button_state == button_up)
					{
						run_function(on_down_function, on_down_object);
						button_state = button_down;
					}
				}
				else
				{
					run_function(on_down_function, on_down_object);
				}
			}
			button_entered_down = false;
		}
	}
	
	public class Element
	{
		int mouse_none = 0;
		int mouse_down = 1;
		int mouse_over = 2;
		
		int clicked = 3;
		int dragged = 4;
		int moved = 5;
		int pressed = 1;
		int released = 2;
		
		float x, y, w, h;
		
		int mouse_state = mouse_none; // only has 3 states mouse over, down, or nothing
		
		ArrayList<Element> children;
		
		PApplet p;
		
		Element(float x, float y, float w, float h, PApplet p)
		{		
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			
			this.p = p;
		}
		
		void run_function(Method method, Object object)
		{
			try
			{
				method.invoke(object);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Method set_function(Method method, Object object, String method_name)
		{
			try
			{
				method = object.getClass().getMethod(method_name);
				return method;
			}
			catch(Exception e)
			{
				System.err.println(e.getStackTrace());
				return null;
			}
		}
		
		void draw()
		{
			
		}
		
		// Mouse Event Handling 
		void mouse_event(MouseEvent e)
		{
			boolean within_bounds = within_bounds(e.getX(), e.getY());
			int mouse_action = e.getAction();
			
			mouse_state = mouse_none; // Reset the mouse state
			
			if(mouse_action == pressed) { pressed(within_bounds);}
			if(mouse_action == released) { released(within_bounds);}
			if(mouse_action == clicked) { clicked(within_bounds);}
			if(mouse_action == dragged) { dragged(within_bounds);}
			if(mouse_action == moved) { moved(within_bounds);}	
		}
		
		void pressed(boolean within_bounds)
		{
			if(within_bounds){ mouse_state = mouse_down; }
		}
		void released(boolean within_bounds)
		{
			if(within_bounds) { mouse_state = mouse_over; }
			else { mouse_state = mouse_none; }
		}
		void clicked(boolean within_bounds)
		{
			if(within_bounds) { mouse_state = mouse_over; }
		}
		void dragged(boolean within_bounds)
		{
			if(within_bounds) { mouse_state = mouse_down; }
		}
		void moved(boolean within_bounds)
		{
			if(within_bounds) { mouse_state = mouse_over; }
		}
		
		void attach_to(Element element)
		{
			
		}
		
		void detach()
		{
			
		}
		
		/* Checks if a point is within the 
		 * bounding box of the element */	
		boolean within_bounds(float px, float py)
		{
			if(px > x && px < x+w)
			{
				if(py > y && py < y+h)
				{
					return true;
				}
			}
			return false;
		}
	}
	
}
