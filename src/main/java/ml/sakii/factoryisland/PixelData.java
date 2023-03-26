package ml.sakii.factoryisland;

public class PixelData {
	double depth=0;
	int color=0;
	double overlayDepth=0;
	int overlayColor=0;
	
	public void reset() {
		depth=0;
		color=0;
		overlayDepth=0;
		overlayColor=0;
	}

	@Override
	public String toString() {
		return Math.round(depth*100)/100f+", 0x"+Integer.toHexString(color).toUpperCase()+",dst:"+Math.round(1/depth*100)/100f;//Integer.toString(color,16);
	}
	
}
