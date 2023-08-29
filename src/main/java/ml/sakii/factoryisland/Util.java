package ml.sakii.factoryisland;

public class Util {

	public static double interp(double x1, double x2, double x, double y1, double y2) {
		return y1+(x-x1)*(y2-y1)/(x2-x1);
	}
	
	public static double getSlope(double x1, double x2, double y1, double y2) {
		return (y2-y1)/(x2-x1);
	}
	
	public static double interpSlope(double x1, double x, double y1, double slope) {
		return y1+(x-x1)*slope;
	}
	
	public static int wrap(int value, int min, int max) {
		return Math.floorMod(value , (max-min+1))+min;
		
	}
	
	public static float[] rotateCoordinates(float x, float y,float centerX, float centerY, float angle){
		float newX = (float)(   ((x-centerX)*Math.cos(angle) - (y-centerY)*Math.sin(angle)) + centerX   );
		float newY = (float)(   ((x-centerX)*Math.sin(angle) + (y-centerY)*Math.cos(angle)) + centerY   );
		return new float[]{newX, newY};
	}

	public static int limit(int value, int min, int max) {
		if(value>max) {
			return max;
		}
		if(value<min) {
			return min;
		}
		return value;
	}
	
	public static double limit(double value, double min, double max) {
		if(value>max) {
			return max;
		}
		if(value<min) {
			return min;
		}
		return value;
	}
	
	/** @param coneOrigin IMMUTABLE */
	public static boolean sphereCone(Vector sphereCenter, float sphereRadius, Vector coneOrigin, Vector coneNormal, float sinAngle, float tanAngleSqPlusOne, Vector tmpVector)
	{
	    Vector diff = coneOrigin.multiply(-1).add(sphereCenter);
	    
	    if(tmpVector.set(coneNormal).multiply(sinAngle).multiply(sphereRadius).multiply(-1).add(diff).DotProduct(coneNormal) > 0) {
	    	//Vector c = diff.multiply(sinAngle).add(coneNormal.cpy().multiply(sphereRadius));
	    	Vector c = diff.multiply(sinAngle).add(tmpVector.set(coneNormal).multiply(sphereRadius));
	    	double lenA = c.DotProduct(coneNormal);
	    	
	    	return c.getLengthSq() <= lenA*lenA*tanAngleSqPlusOne;
	    }else {
	    	return diff.getLengthSq() <= sphereRadius*sphereRadius;
	    }
	}
	

}
