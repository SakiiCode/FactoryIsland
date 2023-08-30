package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Star extends Object3D {
	
	private static final float MAX_SIZE=5f;
	
	final Vector pos;

	private final float size;
	private final Point p = new Point();
	private final Color4 c;
	

	public Star() {
		float x = (float) (Math.random() * 2 - 1);
		float y = (float) (Math.random() * 2 - 1);
		float z = (float) (Math.random() * 2 - 1);
		pos = new Vector(x, y, z);
		size = (float) (Math.random() * MAX_SIZE);
		AvgDist = 1000;
		c = new Color4((float)(0.5+Math.random()*0.5), (float)(0.5+Math.random()*0.5), (float) (0.5+Math.random()*0.5f), size/MAX_SIZE);
	}

	public Star(int size) {
		pos = new Vector(1, 0, 0.5f);
		this.size = size;
		c= new Color4(Color.WHITE);
	}

	@Override
	protected boolean update(UpdateContext context) {
		if (context.game.ViewVector.DotProduct(pos) > 0 || (size < MAX_SIZE && Math.random() < 0.001f)) {
			return false;
		}

		context.tmpVector.set(pos).add(context.game.PE.getPos());
		context.game.convert3Dto2D(context.tmpVector, p);
		return true;
	}

	@Override
	protected void draw(Graphics g, Game game) {
		int size = (int) (Config.resolutionScaling * this.size);
		if (Config.fogEnabled) {
			g.setColor(Color.GRAY);
		} else {
			g.setColor(c.getColor());
		}
		g.fillOval(p.x, p.y, size, size);
	}

}
