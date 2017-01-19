import java.util.ArrayList;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.core.*;

public class AnimData{

	PApplet p;
	
	ArrayList<PVector> points;	
	
	AnimData(PApplet p)
	{
		this.p = p;
	}
	
	// Graphics
	void draw()
	{
		if(points != null)
		{
			p.pushStyle();
			for(int d=0; d<points.size(); d++)
			{
				p.strokeWeight(1);
				
				if(d == points.size()-1)
				{
					p.stroke(255, 50);
					p.line(points.get(d).x, points.get(d).y, points.get(0).x, points.get(0).y);
				}
				else
				{
					p.stroke(255);
					p.line(points.get(d).x, points.get(d).y, points.get(d+1).x, points.get(d+1).y);
				}
				p.fill(0);
				p.ellipse(points.get(d).x, points.get(d).y,2, 2);
			}
			p.popStyle();
		}
	}
	
	void draw_loop()
	{
		if(points != null)
		{
			if(points.size() > 3)
			{
				p.pushStyle();
				p.noFill();
				p.stroke(255,0,0);
				p.strokeWeight(1);
				
				PVector[] bezier_join = join_ends_with_bezier();
				
				p.bezier(bezier_join[0].x, bezier_join[0].y, 
						 bezier_join[1].x, bezier_join[1].y, 
						 bezier_join[2].x, bezier_join[2].y, 
						 bezier_join[3].x, bezier_join[3].y);
				
			    p.popStyle();
			}
		}
	}
	
	// Point Operations
	PVector get_point(int index)
	{
		if(points == null)
		{
			return null;
		}
		else
		{
			return points.get(index);
		}
	}
	  
	void add_point(float x, float y)
	{
		points.add(new PVector(x, y));  
	}
	  
	void clear_points()
	{
		points = new ArrayList<PVector>();
	}
	
	int size()
	{
		return points.size();
	}
	
	// Data
	void saveToJson()
	  {
	    JSONArray  json_points = new JSONArray();
	    JSONObject json_out = new JSONObject();
	    
	    for(int p=0; p< points.size(); p++)
	    {
	      JSONArray  json_point = new JSONArray();
	      float norm_x = PApplet.map(points.get(p).x, 0, 500, 0, 1);
	      float norm_y = PApplet.map(points.get(p).y, 25, 525, 0, 1);
	      
	      json_point.setFloat(0, norm_x);
	      json_point.setFloat(1, norm_y);
	      json_points.setJSONArray(p, json_point);
	    }
	    json_out.setJSONArray("points", json_points);
	      
	    p.saveJSONObject(json_out, "Export_" + PApplet.year() + "_" + PApplet.month() + "_" + PApplet.day() + "_" + p.millis() + ".json");
	  }
	  
	void loadJson(String file)
	{
		this.clear_points();
	    
	    JSONObject data = p.loadJSONObject(file);
	    JSONArray data_points = data.getJSONArray("points");
	    
	    for(int p=0; p<data_points.size(); p++)
	    {
	       JSONArray point = data_points.getJSONArray(p);
	       float mapped_x = PApplet.map(point.getFloat(0), 0, 1, 0, 500);
	       float mapped_y = PApplet.map(point.getFloat(1), 0, 1, 25, 525);
	       this.add_point(mapped_x, mapped_y);
	    }
	}
	
	// Operations
	
	// Returns a bezier curve that connects both ends
	PVector[] join_ends_with_bezier()
    {
		PVector[] bezier_points = null; 
    
	    if(points.size() >= 4)
	    {
	    	bezier_points = new PVector[4];
		  
		    // Grab the first and last point
		    PVector first_point = points.get(0).copy();
		    PVector last_point  = points.get(points.size()-1).copy();
		
		    // Find the distance between the points. The further the distance, the larger the curve drawn
		    float dist = PApplet.dist(last_point.x, last_point.y, first_point.x, first_point.y);
		    float control_point_offset = dist/3;
		  
		    // Find the control points
		    PVector first_control_point = first_point.copy();
		    PVector last_control_point  = last_point.copy();
		    first_control_point = first_control_point.add(line_end_angle(+1, points, 55f).mult(control_point_offset));
		    last_control_point = last_control_point.add(line_end_angle(-1, points, 55f).mult(control_point_offset));
		    
		    bezier_points[0] = first_point;
		    bezier_points[1] = first_control_point;
		    bezier_points[2] = last_control_point;
		    bezier_points[3] = last_point;
	    }
	    return bezier_points;
    }
	
	// Returns a rough vector angle of the start or end segment of a line
	// A minimum distance is used to filter this result.
	// Use direction to indicate which end of the line to compute from.
	PVector line_end_angle(int direction, ArrayList<PVector> points, float min_distance)
	{
		int points_checked = 0;
		float distance_traversed = 0;
		PVector angle = new PVector(0,0);
		
		int point_index = 0;
		int point_increment = 1;
		
		if(direction < 0)
		{
			point_index = points.size()-1;
			point_increment = -1;
		}
		
	    for(int p=point_index; points_checked<points.size()-1; p+=point_increment)
	    {
	      PVector cur = points.get(p);
		  PVector nxt = points.get(p+point_increment);
		  
		  distance_traversed += PApplet.dist(cur.x, cur.y, nxt.x, nxt.y);
	      
	      if(distance_traversed > min_distance || points_checked == points.size()-1)
	      {
	    	  angle = points.get(point_index).copy();
		      angle = angle.sub(points.get(p));
		      
		      break;
	      }
	      points_checked++;
	    }
	    return angle.normalize();
	  }
	
	
	ArrayList<PVector> get_loop(ArrayList<PVector> points)
	{
		PVector[] bezier_join = join_ends_with_bezier();
		ArrayList<PVector> return_loop = null;
	    
	    if(bezier_join != null)
	    {      
	      // Get the rough distance of the bezier curve, we will need this later
	      float bezier_distance = 0;
	      int bezier_division = 500; // 500 should be more than enough, the more accurate the better
	      
	      for(int d=0; d<bezier_division; d++)
	      {
	        float segment_start_position = d/500;
	        float segment_end_postion = (d+1)/500;
	        
	        // Find the start coordinates of the segment
	        float start_x = p.bezierPoint(bezier_join[0].x, bezier_join[1].x, bezier_join[2].x, bezier_join[3].x, segment_start_position);
	        float start_y = p.bezierPoint(bezier_join[0].y, bezier_join[1].y, bezier_join[2].y, bezier_join[3].y, segment_start_position);
	        // Find the end coordinates of the segment
	        float end_x = p.bezierPoint(bezier_join[0].x, bezier_join[1].x, bezier_join[2].x, bezier_join[3].x, segment_end_postion);
	        float end_y = p.bezierPoint(bezier_join[0].y, bezier_join[1].y, bezier_join[2].y, bezier_join[3].y, segment_end_postion);
	        
	        bezier_distance += PApplet.dist(start_x, start_y, end_x, end_y);
	      }

	      // Find start and end speed. This is based on the length of either end segments
	      PVector start_speed_pt1  = points.get(0);
	      PVector start_speed_pt2 = points.get(1);
	      
	      PVector end_speed_pt1  = points.get(points.size()-1);
	      PVector end_speed_pt2 = points.get(points.size()-2);
	      
	      float start_speed = PApplet.dist(start_speed_pt1.x, start_speed_pt1.y, start_speed_pt2.x, start_speed_pt2.y);
	      float end_speed = PApplet.dist(end_speed_pt1.x, end_speed_pt1.y, end_speed_pt2.x, end_speed_pt2.y);
	      
	      // Now interpolate the speeds across the bezier curve
	      
	      // Interpolation concept:
	      // - Imagine that we fit the same number of 2 different sized segments on a line
	      // - It does not matter what way we organise them, since the total distance is the same
	      // - This is also true if we randomly resize the segments - as long as we remove as much as we add (net zero change)
	      
	      // First calculate how many division the curve will need
	      int bezier_divisions = PApplet.round(bezier_distance/(start_speed + end_speed)); // There was a div2 here?
	      float division_increment = (start_speed-end_speed)/bezier_divisions; 
	      float division_start_length = end_speed; // Reminder that we are interpolating from the end to the start, hence the reverse order
	         
	      float position_offset = 0;
	      
	      return_loop = new ArrayList<PVector>();
	      
	      for(int i=1; i<bezier_divisions-1; i++)
	      {
	    	  float cur_division_length = division_start_length + (division_increment*i);
	    	  float cur_division_ratio = cur_division_length/bezier_distance*2; // Find out ratio along bezier
	    	  float cur_division_position = cur_division_ratio + position_offset;
	    	  
	    	  // Find bezier point
	    	  float x = p.bezierPoint(bezier_join[3].x, bezier_join[2].x, bezier_join[1].x, bezier_join[0].x, cur_division_position);
	    	  float y = p.bezierPoint(bezier_join[3].y, bezier_join[2].y, bezier_join[1].y, bezier_join[0].y, cur_division_position);
	        
	    	return_loop.add(new PVector(x, y));
	        position_offset += cur_division_ratio; // Add to the exisiting offset
	      }
	    }
	    return return_loop;
	  }
}
