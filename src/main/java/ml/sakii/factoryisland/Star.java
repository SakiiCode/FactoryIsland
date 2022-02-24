package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.geom.Point2D;
public class Star {
	final Vector pos;
	
	private final Vector pos2=new Vector();
	private final float size;
	private final Game game;
	private final Point2D.Float p = new Point2D.Float();
	
	public Star(Game game) {
		pos = new Vector(
				(float)(Math.random()*2-1),
				(float)(Math.random()*2-1),
				(float)(Math.random()*2-1));
		size = (float)(Math.random()*10);
		this.game = game;
	}
	
	public Star(Game game, int size) {
		pos = new Vector(1,0,0.5f);
		this.size = size;
		this.game = game;
	}
	
	void draw(Graphics g) {
		
		if(game.ViewVector.DotProduct(pos) > 0) {
			pos2.set(pos).add(game.PE.getPos());
			game.convert3Dto2D(pos2,p);
			g.fillOval((int)p.getX(), (int)p.getY(), (int)(size*game.ratio), (int)(size*game.ratio));
		}
	}
	
	
}
