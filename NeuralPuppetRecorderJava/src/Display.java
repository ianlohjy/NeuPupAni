import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class Display extends Gui.Element{

	Animation a;
	int w, h;
	PGraphics preview_frame;
	
	Display(PApplet p, Animation a)
	{
		super(0, 0, 0, 0, p);
		this.a = a;
	}

	@Override
	void draw()
	{	
		x = w = h = p.width/2;
		y = 25;
		
		// Clipping
		p.clip(x,  y,  w,  h);
		
		// Background
		p.pushStyle();
		p.noStroke();
		p.fill(255);
		p.rectMode(PApplet.CORNER);
		p.rect(w,25,w,h);
		// Display interpolated face
		show_animation_result();
		p.popStyle();
		
		p.noClip();
	}
	
	void show_animation_result()
	{
		if(preview_frame == null)
		{
			if(w == 0 || h ==0)
			{
				preview_frame = p.createGraphics(500, 500);
			}
			else
			{
				preview_frame = p.createGraphics(w, h);
			}
		}
		
		preview_frame.beginDraw();
		//preview_frame.background(0);
		
		PImage display_image = a.get_face_grid();
		int[] face_grid_shape = a.get_face_grid_shape();
		PVector current_playback_position = a.get_current_playback_position();
		
		if(display_image !=null)
		{
			// First crop the values to be in range of the canvas area
			float grid_x = Utilities.crop(current_playback_position.x, 0, w);
			float grid_y = Utilities.crop(current_playback_position.y, 0, h);
			
			int padding = 25;
			
			grid_x = grid_x/w * face_grid_shape[0];
			grid_y = grid_y/h * face_grid_shape[1];
			
			grid_x = PApplet.floor(grid_x);
			grid_y = PApplet.floor(grid_y);
			
			// Special case for exceeding values
			if(grid_x >= face_grid_shape[0]) { grid_x = face_grid_shape[0]-1;}
			if(grid_y >= face_grid_shape[1]) { grid_y = face_grid_shape[1]-1;}

			float div_w = display_image.width/face_grid_shape[0]; 
			float div_h = display_image.height/face_grid_shape[1]; 
			
			preview_frame.copy(display_image, 
				   (int)((grid_x*div_w)+padding),
				   (int)((grid_y*div_h)+padding*1.5), 
				   (int)div_w-(padding*2), 
				   (int)div_h-(padding*2),
				   (int)0,(int)0, w, h);
		}
		
		preview_frame.endDraw();
		p.image(preview_frame, x, y);
	}
	
	
}
