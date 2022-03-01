package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public abstract class Object3D implements Comparable<Object3D> {
	float AvgDist;
	
	/**
	 * @return true ha lathato
	 */
	protected abstract boolean update(Game game);
	
	protected abstract void draw(BufferedImage FrameBuffer, Graphics g, Game game);
	
	@Override
	public int compareTo(Object3D o) {
			return Float.compare(o.AvgDist, AvgDist);
	}
}
