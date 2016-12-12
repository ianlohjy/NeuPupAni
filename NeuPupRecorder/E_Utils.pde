boolean withinBounds(float x, float y, float w, float h, float x_input, float y_input)
{
  if(x_input > x && x_input < (x+w))
  {
    if(y_input > y && y_input < (y+h))
    {
      return true;
    }
  }
  return false;
}

boolean mouseDown(MouseEvent e)
{
  if(e.getAction() == 1) {return true;}
  else{ return false; }
}

boolean mouseUp(MouseEvent e)
{
  if(e.getAction() == 2) {return true;}
  else{ return false; }
}

ArrayList<PVector> CatmullRomChain(ArrayList<PVector> points, int pointsPerSegment)
{
        ArrayList<PVector> allPoints = new ArrayList<PVector>();

        if(points.size() < 4)
        {
            return allPoints;
        }
            
        for (int p = 0; p < points.size()-1; p++)
        {
            ArrayList<PVector> _points;

            if (p == 0)
            {
                _points = CatmullRom2(points.get(p), points.get(p), points.get(p+1), points.get(p+2), pointsPerSegment);
            }
            else if(p == points.size()-2)
            {
                _points = CatmullRom2(points.get(p-1), points.get(p), points.get(p+1), points.get(p+1), pointsPerSegment);
            }
            else
            {
                _points = CatmullRom2(points.get(p-1), points.get(p), points.get(p+1), points.get(p+2), pointsPerSegment);
            }
            allPoints.addAll(_points);
        }
        return allPoints;
    }
/*
    ArrayList<PVector> CatmullRom(PVector p0, PVector p1, PVector p2, PVector p3, int numPoints )
    {
        //Adapted from https://www.wikiwand.com/en/Centripetal_Catmull%E2%80%93Rom_spline

        ArrayList<PVector> points = new ArrayList<PVector>();

        float t0 = 0f;
        float t1 = GetT(t0, p0, p1);
        float t2 = GetT(t1, p1, p2);
        float t3 = GetT(t2, p2, p3);

        float knot = (t2-t1)/numPoints;
 
        for(int p=0; p<numPoints; p++)
        {
            float _knot = t1+(knot*p);

            PVector A1 = (t1-_knot)/(t1-t0)*p0 + (_knot-t0)/(t1-t0)*p1;
            PVector A2 = (t2-_knot)/(t2-t1)*p1 + (_knot-t1)/(t2-t1)*p2;
            PVector A3 = (t3-_knot)/(t3-t2)*p2 + (_knot-t2)/(t3-t2)*p3;

            PVector B1 = (t2-_knot)/(t2-t0)*A1 + (_knot-t0)/(t2-t0)*A2;
            PVector B2 = (t3-_knot)/(t3-t1)*A2 + (_knot-t1)/(t3-t1)*A3;

            PVector C = (t2-_knot)/(t2-t1)*B1 + (_knot-t1)/(t2-t1)*B2;

            points.add(C);
        }
        return points;
    }*/

    ArrayList<PVector> CatmullRom2(PVector p0, PVector p1, PVector p2, PVector p3, int numPoints)
    {
        ArrayList<PVector> points = new ArrayList<PVector>();

        float t0 = 0.0f;
        float t1 = GetT(t0, p0, p1);
        float t2 = GetT(t1, p1, p2);
        float t3 = GetT(t2, p2, p3);

        for(float t=t1; t<t2; t+=((t2-t1)/numPoints))
        {
            PVector A1 = p0.mult((t1-t)/(t1-t0)).add(p1.mult((t-t0)/(t1-t0)));
            PVector A2 = p1.mult((t2-t)/(t2-t1)).add(p2.mult((t-t1)/(t2-t1)));
            PVector A3 = p2.mult((t3-t)/(t3-t2)).add(p3.mult((t-t2)/(t3-t2)));

            PVector B1 = A1.mult((t2-t)/(t2-t0)).add(A2.mult((t-t0)/(t2-t0)));
            PVector B2 = A2.mult((t3-t)/(t3-t1)).add(A3.mult((t-t1)/(t3-t1)));

            PVector C = B1.mult((t2-t)/(t2-t1)).add(B2.mult((t-t1)/(t2-t1)));

            points.add(C);
        }
        return points;
    }

float GetT(float t, PVector p0, PVector p1)
{
    float alpha = 0.0f;
    float a = pow((p1.x - p0.x), 2);
    float b = pow((p1.y - p0.y), 2);

    float c = pow(sqrt(a + b), alpha) + t;
    return c;
}