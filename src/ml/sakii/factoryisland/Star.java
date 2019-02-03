package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.geom.Point2D;

public class Star {
	final Vector pos, pos2=new Vector();
	final float size;
	final Game game;
	
	public Star(Game game) {
		//int face = (int)(Math.random()*5);
		//this.face = BlockFace.values[face];
		/*x = this.face.direction[0] != 0 ? this.face.direction[0] : (float)(Math.random()*2-1);
		y = this.face.direction[1] != 0 ? this.face.direction[1] : (float)(Math.random()*2-1);
		z = this.face.direction[2] != 0 ? this.face.direction[2] : (float)(Math.random()*2-1);*/
		pos = new Vector(
				(float)(Math.random()*2-1),
				(float)(Math.random()*2-1),
				(float)(Math.random()*2-1));
		size = (float)(Math.random()*10);
		this.game = game;
	}
	
	public void draw(Graphics g) {
		
		if(game.ViewVector.DotProduct(pos) > 0) {
			pos2.set(pos).add(game.PE.getPos());
			Point2D p = game.convert3Dto2D(pos2);
			g.fillOval((int)p.getX(), (int)p.getY(), (int)(size*game.ratio), (int)(size*game.ratio));
		}
	}
	
	
}
