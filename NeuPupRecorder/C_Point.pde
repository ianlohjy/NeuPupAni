class Point
{
  PVector position;
  boolean delete;
  
  Point(float x, float y)
  {
    delete = false; // Used to mark points for deletion
    position = new PVector(x, y);
  }
  
  float x()
  {
    return position.x;
  }
  
  float y()
  {
    return position.y;
  }
  
  void mouseEvent(MouseEvent event)
  {
  }
  
  void draw()
  {
  }
  
  void delete()
  {
  }  
  
  void lineTo(Point point)
  {
    line(x(), y(), point.x(), point.y());
  }
}