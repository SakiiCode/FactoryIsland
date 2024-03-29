package ml.sakii.factoryisland;

import java.awt.Graphics;

public abstract class Object3D implements Comparable<Object3D> {
	float AvgDist;
	
	/**
	 * @return true ha lathato
	 */
	protected abstract boolean update(UpdateContext context);
	
	protected abstract void draw(Graphics g, Game game);
	
	@Override
	public int compareTo(Object3D o) {
			return Float.compare(o.AvgDist, AvgDist);
	}
}
