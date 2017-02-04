import processing.core.*;
import processing.event.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
		Button new_button = new Button(p);
		elements.add(new_button);
		return new_button;
	}
	
	Slider slider()
	{
		Slider new_slider = new Slider(p);
		elements.add(new_slider);
		return new_slider;
	}
	
	public class Slider extends Element
	{
		String label = "";
		
		Object on_change_object;
		Method on_change_function;
		
		int[] slider_down_colour = {0,0,0,0}; // When slider is pressed
		int[] slider_over_colour = {0,0,0,0}; // When slider is hovered
		int[] base_down_colour = {0,0,0,0}; // When base is pressed
		int[] base_over_colour = {0,0,0,0}; // When base is hovered
		
		int[] slider_up_colour = {0,0,0,0}; // Slider default colour
		int[] base_up_colour = {0,0,0,0}; // Base default colour
		
		int[] cur_base_colour;
		int[] cur_slider_colour;
		
		boolean dragged_started = false;
		
		float value = 0.5f; // Out of 1 
		
		Slider(PApplet p)
		{
			super(0, 0, 0, 0, p);
		}
		
		@Override
		void draw()
		{
			clamp_slider_value();
			
			if(mouse_state == mouse_over) 
			{
				cur_base_colour = base_over_colour;
				cur_slider_colour = slider_over_colour;
			}
			else if(mouse_state == mouse_down)
			{
				cur_base_colour = base_down_colour;
				cur_slider_colour = slider_down_colour;
			}
			else
			{
				cur_base_colour = base_up_colour;
				cur_slider_colour = slider_up_colour;
			}
			
			// Slider base
			p.pushStyle();
			p.fill(cur_base_colour[0], cur_base_colour[1], cur_base_colour[2], cur_base_colour[3]);
			p.noStroke();
			p.rectMode(PApplet.CORNER);
			p.rect(x, y, w, h);
			
			// Slider bar
			p.fill(cur_slider_colour[0], cur_slider_colour[1], cur_slider_colour[2], cur_slider_colour[3]);
			p.rect(x, y, value*w, h);
			
			// Text Label
			p.fill(255, 255, 255);
			p.textAlign(PApplet.CENTER, PApplet.CENTER);
			p.text(label, x+(w/2), y+(h/2)); 
			p.popStyle();
		}
		
		void clamp_slider_value()
		{
			if(value > 1) {value = 1;}
			else if(value < 0) {value = 0;}
		}
		
		float value()
		{
			return this.value;
		}
		
		void value(float value)
		{
			this.value = value;
		}
		
		// Setting Attributes
		Slider label(String label)
		{
			this.label = label;
			return this;
		}
		
		Slider x(int x)
		{
			this.x = x;
			return this;
		}
		
		Slider y(int y)
		{
			this.y = y;
			return this;
		}
		
		Slider width(int width)
		{
			this.w = width;
			return this;
		}
		
		Slider height(int height)
		{
			this.h = height;
			return this;
		}
		
		Slider slider_down_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			slider_down_colour = new_colour;
			return this;
		}
		
		Slider slider_up_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			slider_up_colour = new_colour;
			return this;
		}
		
		Slider slider_over_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			slider_over_colour = new_colour;
			return this;
		}
		
		Slider base_down_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			base_down_colour = new_colour;
			return this;
		}
		
		Slider base_up_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			base_up_colour = new_colour;
			return this;
		}
		
		Slider base_over_colour(int r, int g, int b, int a)
		{
			int[] new_colour = {r,g,b,a};
			base_over_colour = new_colour;
			return this;
		}
		
		@Override
		void pressed(boolean within_bounds, MouseEvent e)
		{
			super.pressed(within_bounds, e);
			
			if(within_bounds)
			{
				dragged_started = true;
				int new_value = (int)(e.getX()-x);
				value(new_value/w);
				clamp_slider_value();
				
				run_callback_pass(on_change_function, on_change_object);
			}
		}
		
		@Override
		void released(boolean within_bounds, MouseEvent e)
		{
			super.pressed(within_bounds, e);
			dragged_started = false;
		}
		
		@Override
		void dragged(boolean within_bounds, MouseEvent e)
		{
			super.dragged(within_bounds, e);
			
			if(dragged_started)
			{
				int new_value = (int)(e.getX()-x);
				value(new_value/w);
				clamp_slider_value();
				run_callback_pass(on_change_function, on_change_object);
			}
		}
		
		Slider on_change_function(String function, Object object)
		{
			on_change_object = object;
			on_change_function = set_callback_pass(on_change_function, object, function);
			return this;
		}
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
		
		String current_label = "";
		String up_label = "";
		String over_label = "";
		String down_label = "";
		
		Button(PApplet p)
		{
			super(0, 0, 0, 0, p);
		}
		
		// Drawing
		@Override
		void draw()
		{
			if(mouse_state == mouse_over) {current_colour = over_colour; current_label = over_label;}
			else if(mouse_state == mouse_down) {current_colour = down_colour; current_label = down_label;}
			else 
			{ 
				if(button_state == button_down)
				{
					current_colour = down_colour;
					current_label = down_label;
				}
				else if(button_state == button_up)
				{
					current_colour = up_colour;
					current_label = up_label;
				}
			}
			
			p.pushStyle();
			p.noStroke();
			p.fill(current_colour[0], current_colour[1], current_colour[2], current_colour[3]);
			p.rectMode(PApplet.CORNER);
			p.rect(x, y, w, h);
			
			p.fill(255, 255, 255);
			p.textAlign(PApplet.CENTER, PApplet.CENTER);
			p.text(current_label, x+(w/2), y+(h/2)); 
			
			p.popStyle();
		}
		
		// Setting attributes
		Button down_label(String label)
		{
			this.down_label = label;
			return this;
		}
		
		Button over_label(String label)
		{
			this.over_label = label;
			return this;
		}
		
		Button up_label(String label)
		{
			this.up_label = label;
			return this;
		}
		
		Button label(String label)
		{
			down_label(label);
			over_label(label);
			up_label(label);
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
		
		Button on()
		{
			button_state = button_down;
			return this;
		}
		
		Button off()
		{
			button_state = button_up;
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
			on_down_function = set_callback(on_down_function, object, function);
			return this;
		}
		
		Button on_up_function(String function, Object object)
		{
			on_up_object = object;
			on_up_function = set_callback(on_up_function, object, function);
			return this;
		}
		
		@Override
		void pressed(boolean within_bounds, MouseEvent e)
		{
			super.pressed(within_bounds, e);
			
			if(within_bounds)
			{
				button_entered_down = true;
			}
		}
		
		@Override
		void released(boolean within_bounds, MouseEvent e)
		{
			super.released(within_bounds, e);
			
			if(within_bounds && button_entered_down)
			{
				if(button_toggle)
				{
					if(button_state == button_down)
					{
						button_state = button_up;
						run_callback(on_up_function, on_up_object);
					}
					else if(button_state == button_up)
					{
						button_state = button_down;
						run_callback(on_down_function, on_down_object);
					}
				}
				else
				{
					run_callback(on_down_function, on_down_object);
				}
			}
			button_entered_down = false;
		}
	}
	
	public static class Element
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
		
		// Invokes a method, but also passes the element as an argument
		void run_callback_pass(Method method, Object object)
		{
			try
			{
				method.invoke(object, this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		void run_callback(Method method, Object object)
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
		
		Method set_callback_pass(Method method, Object object, String method_name)
		{
			try
			{
				Class[] args = new Class[1];
				args[0] = Element.class;
				method = object.getClass().getDeclaredMethod(method_name, args);
				return method;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		Method set_callback(Method method, Object object, String method_name)
		{
			try
			{
				method = object.getClass().getDeclaredMethod(method_name);
				return method;
			}
			catch(Exception e)
			{
				e.printStackTrace();
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
			
			if(mouse_action == pressed) { pressed(within_bounds, e);}
			if(mouse_action == released) { released(within_bounds, e);}
			if(mouse_action == clicked) { clicked(within_bounds, e);}
			if(mouse_action == dragged) { dragged(within_bounds, e);}
			if(mouse_action == moved) { moved(within_bounds, e);}	
		}
		
		void pressed(boolean within_bounds, MouseEvent e)
		{
			if(within_bounds){ mouse_state = mouse_down; }
		}
		void released(boolean within_bounds,  MouseEvent e)
		{
			if(within_bounds) { mouse_state = mouse_over; }
			else { mouse_state = mouse_none; }
		}
		void clicked(boolean within_bounds,  MouseEvent e)
		{
			if(within_bounds) { mouse_state = mouse_over; }
		}
		void dragged(boolean within_bounds,  MouseEvent e)
		{
			if(within_bounds) { mouse_state = mouse_down; }
		}
		void moved(boolean within_bounds,  MouseEvent e)
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
