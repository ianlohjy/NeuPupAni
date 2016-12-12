class Data
{
  ArrayList<Point> points;
  
  Data()
  {
    points = new ArrayList<Point>();
  }
  
  ArrayList<Point> getPoints()
  {
    return points;
  }
  
  void saveToJson(String file_name)
  {
    JSONArray  json_points = new JSONArray();
    JSONObject json_out = new JSONObject();
    
    for(int p=0; p< points.size(); p++)
    {
      JSONArray  json_point = new JSONArray();
      float norm_x = map(points.get(p).x(), 0, 500, 0, 1);
      float norm_y = map(points.get(p).y(), 25, 525, 0, 1);
      
      json_point.setFloat(0, norm_x);
      json_point.setFloat(1, norm_y);
      json_points.setJSONArray(p, json_point);
    }
    json_out.setJSONArray("points", json_points);
      
    saveJSONObject(json_out, "Export_" + year() + "_" + month() + "_" + day() + "_" + millis() + ".json");
  }
  
  void loadJson(String file)
  {
    this.clearPoints();
    
    JSONObject data = loadJSONObject(file);
    JSONArray data_points = data.getJSONArray("points");
    
    for(int p=0; p<data_points.size(); p++)
    {
       JSONArray point = data_points.getJSONArray(p);
       float mapped_x = map(point.getFloat(0), 0, 1, 0, 500);
       float mapped_y = map(point.getFloat(1), 0, 1, 25, 525);
       this.addPoint(mapped_x, mapped_y);
    }
  }
  
  void draw()
  {
    if(points == null)
    {
     return; 
    }
    
    pushStyle();
    noFill();
    stroke(255);
    strokeWeight(1);
    
    for(int p=0; p<points.size()-1; p++)
    {
      ellipse(points.get(p).position.x, points.get(p).position.y, 2, 2);
      points.get(p).lineTo(points.get(p+1));
    }
    
    popStyle();
  }
  
  void insertPoint(int index, Point point)
  {
    
  }
  
  Point getPoint(int index)
  {
    return points.get(index);
  }
  
  void addPoint(float x, float y)
  {
    points.add(new Point(x, y));  
  }
  
  void clearPoints()
  {
    points = new ArrayList<Point>();
  }
  
}