package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Object3D implements Comparable<Object3D> {
	float AvgDist;
	
	
	@SuppressWarnings("static-method")
	boolean update() {
		return false;
	}
	
	@SuppressWarnings("unused")
	void draw(BufferedImage FrameBuffer, Graphics g) {}
	
	@Override
	public int compareTo(Object3D o) {
			return Float.compare(o.AvgDist, AvgDist);
	}
}
