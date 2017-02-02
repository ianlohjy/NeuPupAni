import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public class Canvas extends Gui.Element{

	Animation a;
	
	Canvas(PApplet p, Animation a)
	{
		super(0, 0, 0, 0, p);
		this.a = a;
	}
	
	@Override
	void draw()
	{
		x = 0;
		y = 25;
		w = p.width/2;
		h = w;
		
		// Clipping
		p.clip(x,  y,  w,  h);
		
		// Background
		p.pushStyle();
		// Background
		p.noStroke();
		p.fill(15);
		p.rectMode(PApplet.CORNER);
		p.rect(x,y,w,h);
		// Background Image
		if(a.face_grid != null)
		{
			p.image(a.face_grid, x, y, w, h);
			p.fill(0, 200);
			p.rect(x,y,w,h);
		}
		p.popStyle();
		
		// Recorded Line
		a.recorded_data.draw(a.current_frame);
		if(a.playback == a.recording)
		{
			a.recorded_data.draw_loop();
		}
		
		// Current Frame
		PVector current_frame_point = a.recorded_data.get_point(a.current_frame);
		if(current_frame_point != null)
		{
			p.pushStyle();
			p.fill(255,0,0);
			p.noStroke();
			p.ellipse(current_frame_point.x, current_frame_point.y, 10, 10);
			
			// Drawing a callout with the current frame time
			String cur_frame_callout = a.current_frame+"";
			p.pushMatrix();
			p.translate(0, 0, 10);
			p.rectMode(p.CENTER);
			p.fill(0);
			p.rect(current_frame_point.x, current_frame_point.y-25, p.textWidth(cur_frame_callout), 20);
			p.fill(255);
			p.textAlign(p.CENTER, p.CENTER);
			p.text(cur_frame_callout, current_frame_point.x, current_frame_point.y-25);
			p.popMatrix();
			
			p.popStyle();
		}
		
		p.noClip();
	}
	
	@Override
	void pressed(boolean within_bounds, MouseEvent e)
	{
		super.pressed(within_bounds, e);

		if(within_bounds)
		{
			a.start_recording(e.getX(), e.getY());
		}
	}
	
	@Override
	void released(boolean within_bounds, MouseEvent e)
	{
		super.released(within_bounds, e);
		
		if(within_bounds || a.playback == a.recording)
		{
			a.stop_recording();
			a.loop_recording();
		}
	}
	
}
