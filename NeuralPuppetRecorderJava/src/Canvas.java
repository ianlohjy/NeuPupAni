import processing.core.PApplet;
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
		
		p.pushStyle();
		// Background
		p.noStroke();
		p.fill(15);
		p.rectMode(PApplet.CORNER);
		p.rect(x,y,w,h);
		p.popStyle();
		
		a.recorded_data.draw();
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
		}
	}
	
}
