
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
}
