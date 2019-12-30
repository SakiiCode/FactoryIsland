package ml.sakii.factoryisland;


public class Util {

	public static double interp(double x1, double x2, double x, double y1, double y2) {
		return y1+(x-x1)*(y2-y1)/(x2-x1);
		//iz1+ratio*(iz2-iz1)
	}
	
	public static double getSlope(double x1, double x2, double y1, double y2) {
		return (y2-y1)/(x2-x1);
	}
	
	public static double interpSlope(double x1, double x, double y1, double slope) {
		return y1+(x-x1)*slope;
	}
	

}
