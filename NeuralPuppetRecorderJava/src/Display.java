import processing.core.PApplet;

public class Display{

	PApplet p;
	int w, h;
	
	Display(PApplet p, Animation a)
	{
		this.p = p;
	}
	
	void draw()
	{
		w = p.width/2;
		h = w;
		
		p.pushStyle();
		// Background
		p.noStroke();
		p.fill(255);
		p.rectMode(PApplet.CORNER);
		p.rect(w,25,w,h);
		p.popStyle();
	}
	
}
