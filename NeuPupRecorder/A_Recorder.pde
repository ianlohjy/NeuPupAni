public class Recorder implements ControlListener 
{
  ControlP5 cp5;
  boolean recording;
  int framerate;
  int last_record;
  Data recorded_data;
  Data player_data;
  PApplet p;
  
  Button play_button;
  boolean playing = false;
  
  int play_cursor = 0;
  int last_frame = 0;
  
  int record_start = 0;
  
  Recorder(PApplet p) 
  {
    this.p = p;
    setupInterface(p);
    recording = false;
    framerate = 25;
    last_record = millis();
    recorded_data = new Data();
    player_data = new Data();
  } 
  
  void mouseEvent(MouseEvent event) 
  { 
    boolean inside_recorder = withinBounds(0, 25, 500, 500, event.getX(), event.getY());
    boolean mouse_down = mouseDown(event);
    boolean mouse_up = mouseUp(event);
    
    if(inside_recorder && mouse_down)
    {
      if(recording == false){ clearPoints(); stop();record_start=millis();} // Clear points if its a new line
        
      recording = true;
    }
    
    if(mouse_up)
    {
      recording = false;
      player_data = loopPath(recorded_data);
      //joinEnds();
    }
  }
  
  void controlEvent(ControlEvent e) {
  }
  
  void play()
  {
    if(!playing)
    {
      play_button.setLabel("STOP"); 
      playing = true;
      last_frame = millis();
      play_cursor = 0;
    }
  }
  
  void stop()
  {
    if(playing)
    {
      play_button.setLabel("PLAY");      
      playing = false;
      play_cursor = 0;
    }
  }

  void playback()
  {
    if(player_data != null)
    {
      // If playing and enough time has passed, update cursor
      if(player_data.points != null && player_data.points.size() > 0)
      { 
        if(playing && (millis()-last_frame) >= (1000/framerate))
        {
          play_cursor++;
          last_frame = millis();
          if(play_cursor > player_data.points.size()-1)
          {
            play_cursor = 0;
          }
        }
      }
      
      // Draw cursor
      if(player_data.points != null && player_data.points.size() > 0)
      { 
        Point cur_point = player_data.points.get(play_cursor);
        pushStyle();
        fill(255,0,0);
        noStroke();
        ellipse(cur_point.position.x, cur_point.position.y, 15, 15);
        popStyle();
      }
    }
    
    float pos_x = 0;
    float pos_y = 0;
    
    if(recording || !playing)
    {
      pos_x = mouseX/500f;
      pos_y = (mouseY-25)/500f; 
    }
    else if(!playing)
    {
      pos_x = mouseX/500f;
      pos_y = (mouseY-25)/500f; 
    }
    
    if(playing)
    {
      if(player_data.points != null && player_data.points.size() > 0)
      {
        Point cur_point = player_data.points.get(play_cursor);
        pos_x = cur_point.position.x/500f;
        pos_y = (cur_point.position.y-25)/500f; 
      }
    }
    
    // Update display
    // println(pos_x, pos_y);
    face.draw(pos_x,pos_y);
  }
  
  void draw()
  {
    pushStyle();
    noStroke();
    fill(0);
    rect(0, 25, 500, 500);
    popStyle();
    
    if(recording)
    {
      player_data = recorded_data;
      
      addPoint(mouseX, mouseY);
      if(recorded_data.points != null)
      {
        play_cursor = player_data.points.size()-1; // set play cursor to last recorded point if recording
      }
    }
    
    if(player_data != null)
    {
      player_data.draw();
    }
    stroke(255);
    strokeWeight(2);
    noFill();
    drawLoopPath(recorded_data);
    
    playback();
  }
  
  // Tries to connect points using a bezier curve.
  // Returns anchor and bezier points
  PVector[] joinEnds(Data data)
  {
    PVector[] bezier_points = null; 
    
    if(data.points.size() >= 4)
    {
      bezier_points = new PVector[4];
      
      // Grab the first and last point
      PVector first_point = data.points.get(0).position.copy();
      PVector last_point  = data.points.get(data.points.size()-1).position.copy();

      // Find the distance between the points
      float dist         = dist(last_point.x, last_point.y, first_point.x, first_point.y);
      float curve_factor = dist/3;
      
      // Make a copy of the points and reverse them
      ArrayList<Point> reversed = new ArrayList<Point>(data.points);
      Collections.reverse(reversed);
      
      // Get tangents at both ends
      PVector first_control = first_point.copy().add(angleAtEnd(data.points, 15f).mult(curve_factor));
      PVector last_control  = last_point.copy().add(angleAtEnd(reversed, 15f).mult(curve_factor));
      
      bezier_points[0] = first_point;
      bezier_points[1] = first_control;
      bezier_points[2] = last_control;
      bezier_points[3] = last_point;
    }
    return bezier_points;
  }
  
  void drawLoopPath(Data data)
  {
    PVector[] bezier = joinEnds(data);
    
    if(bezier != null)
    {
      // Draw the bezier
      pushStyle();
      noFill();
      strokeWeight(1f);
      stroke(255,0,0);
      
      // Draw tangents
      line(bezier[0].x, bezier[0].y, bezier[1].x, bezier[1].y);
      line(bezier[3].x, bezier[3].y, bezier[2].x, bezier[2].y);
      
      strokeWeight(0.5f);
      stroke(255);
      
      // Draw curve
      bezier(bezier[0].x, bezier[0].y, bezier[1].x, bezier[1].y, bezier[2].x, bezier[2].y, bezier[3].x, bezier[3].y);
      popStyle();
    }
  }
  
  Data loopPath(Data data)
  {
    PVector[] bezier = joinEnds(data);
    Data return_data = null;
    
    if(bezier != null)
    {
      return_data = new Data();
      return_data.points = new ArrayList<Point>(data.points);
      
      // Get est. the distance of the curve
      // We will need this later
      float distance = 0;
      
      for(int d=0; d<500; d++)
      {
        float cur_amount = d/500;
        float nxt_amount = (d+1)/500;
        
        float cur_x = bezierPoint(bezier[0].x, bezier[1].x, bezier[2].x, bezier[3].x, cur_amount);
        float cur_y = bezierPoint(bezier[0].y, bezier[1].y, bezier[2].y, bezier[3].y, cur_amount);
        
        float nxt_x = bezierPoint(bezier[0].x, bezier[1].x, bezier[2].x, bezier[3].x, nxt_amount);
        float nxt_y = bezierPoint(bezier[0].y, bezier[1].y, bezier[2].y, bezier[3].y, nxt_amount);
        
        distance += dist(cur_x, cur_y, nxt_x, nxt_y);
      }
      //println("Curve est distance is " + distance);
      
      // Find start and end speed
      PVector start_pt  = data.points.get(0).position;
      PVector start2_pt = data.points.get(1).position;
      
      PVector end_pt  = data.points.get(data.points.size()-1).position;
      PVector end2_pt = data.points.get(data.points.size()-2).position;
      
      float start_speed = dist(start_pt.x, start_pt.y, start2_pt.x, start2_pt.y);
      float end_speed = dist(end_pt.x, end_pt.y, end2_pt.x, end2_pt.y);
      
      int num_divisions = round(distance/(start_speed + end_speed)*2);
      float start_seg = end_speed/distance; 
      float increment = (start_speed-end_speed)/num_divisions/distance; 
      
      //println("Num divs is " + num_divisions + " Start speed is " + start_speed + " End speed is " + end_speed + " Increment is " + increment);
      
      float total_segments = 0;
      
      for(int s=1; s<num_divisions-1; s++)
      {
        float cur_segment = (start_seg)+(increment*s);
        float x = bezierPoint(bezier[3].x, bezier[2].x, bezier[1].x, bezier[0].x, total_segments + cur_segment);
        float y = bezierPoint(bezier[3].y, bezier[2].y, bezier[1].y, bezier[0].y, total_segments + cur_segment);
        
        //println("S" + s + ": " + (total_segments + cur_segment));
        return_data.addPoint(x,y);
        total_segments += cur_segment;
      }
    }
    return return_data;
  }
  
  /*
  void joinEnds2()
  {
    if(data.points.size() >= 4)
    {
      PVector last_point = data.points.get(data.points.size()-1).position;
      PVector first_point = data.points.get(0).position;
      
      ArrayList<Point> reversed = new ArrayList<Point>(data.points);
      Collections.reverse(reversed);
      
      float dist = dist(last_point.x, last_point.y, first_point.x, first_point.y);
      PVector last_control = last_point.copy().add(angleAtEnd(reversed, 15f).mult(dist/4));
      PVector first_control = first_point.copy().add(angleAtEnd(data.points, 15f).mult(dist/4));
      
      //println(last_point, first_point);
      pushStyle();
      noFill();
      strokeWeight(1);
      stroke(255,0,0);
      
      line(last_point.x, last_point.y, last_control.x, last_control.y);
      line(first_point.x, first_point.y, first_control.x, first_control.y);
      popStyle();
      bezier(last_point.x, last_point.y, last_control.x, last_control.y, first_control.x, first_control.y, first_point.x, first_point.y);
    }
  }*/
  
  // Returns the a normalised tangent of the beginning of a set of points
  // A minimum distance threshold is used to avoid bad angles if the points are too close
  PVector angleAtEnd(ArrayList<Point> points, float distance_threshold)
  {
    ArrayList<Point> _points = new ArrayList<Point>(points);
    float distance = 0;
    PVector angle = new PVector(0,0);
    
    pushStyle();
    for(int p=0; p<_points.size()-1; p++)
    {
      PVector cur = _points.get(p).position;
      PVector nxt = _points.get(p+1).position;
      
      noStroke();
      fill(0,255,0);
      ellipse(cur.x, cur.y, 5, 5);
      ellipse(nxt.x, nxt.y, 5, 5);
      
      distance += dist(cur.x, cur.y, nxt.x, nxt.y);
      angle = cur.copy();
      angle = angle.sub(nxt);
      
      //angle = _points.get(0).position.copy();
      //angle = angle.sub(_points.get(p).position);
      
      if(distance > distance_threshold)
      {
        break;
      }
    }
    popStyle();
    //println(angle);
    return angle.normalize();
  }
  
  /*
  void joinEnds()
  {
    if(data.points.size() >= 4)
    {
      int last_pos = data.points.size()-1;
      int divisions = 15;
      float length_per_div = 1f/divisions;
      
      for(int d=0; d<divisions+1; d++)
      {
        float x = curvePoint(data.getPoint(last_pos-1).x(), data.getPoint(last_pos).x(), data.getPoint(0).x(), data.getPoint(1).x(), d*length_per_div);
        float y = curvePoint(data.getPoint(last_pos-1).y(), data.getPoint(last_pos).y(), data.getPoint(0).y(), data.getPoint(1).y(), d*length_per_div);
        
        addPoint(x, y);
      }
    }
  }*/
  
  void addPoint(float x, float y)
  {
    float time_per_frame = 1000f/framerate;
    
    if(millis()-last_record >= time_per_frame)
    {
      if(x > 500) {x=500;} // Clamp x to recorder area
      if(x < 0)   {x=0;} 
      if(y > 525) {y=525;} // Clamp y to recorder area
      if(y < 25)   {y=25;} 
            
      //println(((millis()-record_start)/1000) + " " + recorded_data.points.size());
            
      recorded_data.addPoint(x, y);
      last_record = millis();
    } 
  }
  
  void selectData()
  {
    selectInput("Find a point.json file to load", "loadData", null, this);
  }
  
  public void loadData(File selection)
  {
     stop();
     if(selection != null)
     {
       recorded_data.loadJson(selection.getAbsolutePath());
       player_data = loopPath(recorded_data);
     }
  }
  
  void clearPoints()
  {
    stop();
    recorded_data.clearPoints();
    if(player_data != null)
    {
      player_data.clearPoints();
    }
  }
  
  void exportData()
  {
    player_data.saveToJson("not_used_for_now");
  }
  
  void playButton()
  {
    if(playing)
    {
      stop();
    }
    else
    {
      play();
    }
  }
  
  void setupInterface(PApplet p)
  {
    cp5 = new ControlP5(p);

    CColor c = new CColor(50, 50, 50, 0, 0);

    cp5.begin();

    cp5.addButton("clear")
      .setCaptionLabel("ERASE")
      .setPosition(0, 0)
      .setSize(150, 25)
      .setValue(250)
      .setColorValue(color(255))
      .setColorActive(color(100))
      .setColorForeground(color(175))
      .setColorBackground(color(0, 0, 0))
      .plugTo(this,"clearPoints");
      ;
      
    play_button = cp5.addButton("play")
      .setCaptionLabel("PLAY")
      .setPosition(150, 0)
      .setSize(150, 25)
      .setValue(250)
      .setColorValue(color(255))
      .setColorActive(color(100))
      .setColorForeground(color(175))
      .setColorBackground(color(0, 0, 0))
      .plugTo(this,"playButton");
      ;  
     
     cp5.addButton("export")
      .setCaptionLabel("EXPORT")
      .setPosition(300, 0)
      .setSize(200, 25)
      .setValue(250)
      .setColorValue(color(255))
      .setColorActive(color(100))
      .setColorForeground(color(175))
      .setColorBackground(color(0, 0, 0))
      .plugTo(this,"exportData");
      ;  
      
     cp5.addButton("load")
      .setCaptionLabel("load")
      .setPosition(500, 0)
      .setSize(200, 25)
      .setValue(250)
      .setColorValue(color(255))
      .setColorActive(color(100))
      .setColorForeground(color(175))
      .setColorBackground(color(0, 0, 0))
      .plugTo(this,"selectData");
      ;  
    
    /*
    cp5.addToggle("lines")
     .setPosition(10,50)
     .setSize(80,20)
     .setMode(Toggle.SWITCH)
     ;
     */
    cp5.end();
    cp5.addListener(this);
  }
}