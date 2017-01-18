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
			p.pushMatrix();
			for(int d=0; d<points.size()-1; d++)
			{
				p.strokeWeight(1);
				p.stroke(255);
				p.line(points.get(d).x, points.get(d).y, points.get(d+1).x, points.get(d+1).y);
				p.fill(0);
				p.ellipse(points.get(d).x, points.get(d).y,5, 5);
			}
			p.popMatrix();
		}
	}
	
	// Point Operations
	PVector getPoint(int index)
	{
		return points.get(index);
	}
	  
	void addPoint(float x, float y)
	{
		points.add(new PVector(x, y));  
	}
	  
	void clearPoints()
	{
		points = new ArrayList<PVector>();
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
	    this.clearPoints();
	    
	    JSONObject data = p.loadJSONObject(file);
	    JSONArray data_points = data.getJSONArray("points");
	    
	    for(int p=0; p<data_points.size(); p++)
	    {
	       JSONArray point = data_points.getJSONArray(p);
	       float mapped_x = PApplet.map(point.getFloat(0), 0, 1, 0, 500);
	       float mapped_y = PApplet.map(point.getFloat(1), 0, 1, 25, 525);
	       this.addPoint(mapped_x, mapped_y);
	    }
	  }
}
