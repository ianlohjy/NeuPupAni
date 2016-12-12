class Face
{
  PImage image;
  int    div_x;
  int    div_y; 
  int    rows;
  int    cols;
  int    size = 500;
  
  Face(String path_to_image, int rows, int cols)
  {
    image = loadImage(path_to_image);
    
    div_x = image.width/cols;
    div_y = image.height/rows;
    
    this.rows = rows;
    this.cols = cols;
  }
  
  void draw(float lerp_x, float lerp_y)
  {
    if(lerp_x < 0){lerp_x = 0;}
    if(lerp_x > 1){lerp_x = 1;}
    if(lerp_y < 0){lerp_y = 0;}
    if(lerp_y > 1){lerp_y = 1;} // Setting to 1 will yeild a blank image
    
    int lx = (int)((lerp_x)*image.width/div_x);
    int ly = (int)((lerp_y)*image.height/div_y);
    
    if(lx > cols-1){lx = cols-1;}
    if(ly > rows-1){ly = rows-1;}
    
    //copy(image, (lx*div_x-1)+(lx), ly*div_y, div_x, div_y, 0, 0, width, height);
    //copy(image, (lx*div_x-1)+(lx), (ly*div_y-1)+(ly), div_x, div_y, (width/2)+((width/4)-(size/2)), (height/2)-(size/2)+20, size, size);
    copy(image, 
        (lx*div_x), 
        (ly*div_y), 
        div_x,
        div_y, 
        (width/2)+((width/4)-(size/2)), 
        25, 
        size, 
        size);
  }
}