import controlP5.*;
import java.util.Collections;

Recorder recorder;
Face face;

void settings()
{
  size(1000,525);
}

void setup()
{
  //face = new Face("map2_02.png", 7, 7);  
  //face = new Face("alison_smile_mouthopen_map.jpg", 7, 7);  
  face = new Face("nips1_smile_mouthopen_map.png", 7, 7);  
  recorder = new Recorder(this);
  frameRate(120);
}

void draw()
{
  background(0);
  recorder.draw();
  //face.draw(0,0);
}

void controlEvent(ControlEvent e) {
}

void mouseEvent(MouseEvent e)
{
  recorder.mouseEvent(e);
}

// Pass mouse events to other objects in sketch
void mousePressed(MouseEvent e){mouseEvent(e);}
void mouseClicked(MouseEvent e){mouseEvent(e);}
void mouseReleased(MouseEvent e){mouseEvent(e);}
void mouseMoved(MouseEvent e){mouseEvent(e);}
void mouseDragged(MouseEvent e){mouseEvent(e);}