package ml.sakii.factoryisland;

public class PixelData {
	double depth=0;
	int color=0;
	
	public void reset() {
		depth=0;
		color=0;
	}

	@Override
	public String toString() {
		return depth+", 0x"+Integer.toHexString(color).toUpperCase();//Integer.toString(color,16);
	}
	
}
