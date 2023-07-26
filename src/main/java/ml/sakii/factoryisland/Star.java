package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
public class Star extends Object3D{
	final Vector pos;
	
	private final Vector pos2=new Vector();
	private final float size;
	private final Point p = new Point();
	
	public Star() {
		pos = new Vector(
				(float)(Math.random()*2-1),
				(float)(Math.random()*2-1),
				(float)(Math.random()*2-1));
		size = (float)(Math.random()*10);
		AvgDist=1000;
	}
	
	public Star(int size) {
		pos = new Vector(1,0,0.5f);
		this.size = size;
	}

	
	//TODO data-oriented
	@Override
	protected boolean update(Game game) {
		pos2.set(pos).add(game.PE.getPos());
		game.convert3Dto2D(pos2,p);
		return game.ViewVector.DotProduct(pos) > 0;
	}

	@Override
	protected void draw(Graphics g, Game game) {
		int size = (int)(Config.resolutionScaling*this.size);
		g.setColor(Color.WHITE);
		g.fillOval(p.x, p.y, size, size);		
	}
	
	
}
