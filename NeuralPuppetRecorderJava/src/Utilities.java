
public class Utilities {

	static boolean within_bounds(float x, float y, float w, float h, float px, float py)
	{
		if(px > x && px < x+w)
		{
			if(py > y && py < y+h)
			{
				return true;
			}
		}
		return false;
	}
	
	static float crop(float value, float limit_low, float limit_high)
	{
		if(value > limit_high) {return limit_high;}
		else if(value < limit_low) {return limit_low;}
		else {return value;}
	}
	
	static int crop(int value, int limit_low, int limit_high)
	{
		if(value > limit_high) {return limit_high;}
		else if(value < limit_low) {return limit_low;}
		else {return value;}
	}
}
