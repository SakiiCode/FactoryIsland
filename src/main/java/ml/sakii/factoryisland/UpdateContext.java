package ml.sakii.factoryisland;

import java.awt.geom.Point2D;

public class UpdateContext {
	Game game;
	
	// placeholder objects for calculations
	Vector tmpVector = new Vector();
	Vector[] tmpArr = new Vector[] {new Vector(),new Vector(), new Vector()};
	Vector2D lineVec = new Vector2D();
	Vector2D pointVec = new Vector2D();
	Point2D.Float[] values = new Point2D.Float[] {new Point2D.Float(),new Point2D.Float(),new Point2D.Float()};

	Vector[] clip2;
	double[][] clipUV2;
	
	public UpdateContext(Game game) {
		this.game = game;
		
		clip2 = new Vector[8];
		clipUV2=new double[8][3];

		for(int i=0;i<clip2.length;i++) {
			clip2[i]=new Vector();
		}
	}
}
