package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
public class Star extends Object3D{
	final Vector pos;
	
	private final Vector pos2=new Vector();
	private final float size;
	private final Point2D.Float p = new Point2D.Float();
	
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

	@Override
	protected boolean update(Game game) {
		return game.ViewVector.DotProduct(pos) > 0;
	}

	@Override
	protected void draw(Graphics g, Game game) {
		float ratio = Config.resolutionScaling;//(Config.getWidth() * 1f / Main.Width	+ Config.getHeight() * 1f / Main.Height) / 2;
		pos2.set(pos).add(game.PE.getPos());
		game.convert3Dto2D(pos2,p);
		g.setColor(Color.WHITE);
		g.fillOval((int)p.getX(), (int)p.getY(), (int)(size*ratio), (int)(size*ratio));		
	}
	
	
}
