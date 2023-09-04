package ml.sakii.factoryisland;

public class Frustum {
	Plane[] sides = new Plane[4];
	Plane tmpnear = new Plane();
	float bigSinAngle, bigTanSq;
	float smallSinAngle, smallTanSq;
	
	private Game game;
	private float hratio, vratio;
	private Vector al, ar, at, ab;

	public Frustum(Game game) {
		this.game = game;
		for (int i = 0; i < 4; i++) {
			sides[i] = new Plane();
		}
		al = new Vector();
		ar = new Vector();
		at = new Vector();
		ab = new Vector();
		update();
	}

	void setRatios(float hratio, float vratio) {
		this.hratio = hratio;
		this.vratio = vratio;
		
		float bigRadius = Config.getDiagonalFOV()*0.5f;
		float smallRadius = (float) Math.toRadians(Config.FOV/2);
		
		Main.log("bigRadius: " + bigRadius + " ("+Math.toDegrees(bigRadius)+"°)");
		Main.log("smallRadius: " + smallRadius + " ("+Math.toDegrees(smallRadius)+"°)");
		
		bigSinAngle = (float) Math.sin(bigRadius);
		bigTanSq = (float) (1 + Math.pow(Math.tan(bigRadius), 2));
		
		smallSinAngle = (float) Math.sin(smallRadius);
		smallTanSq = (float) (1 + Math.pow(Math.tan(smallRadius), 2));
	}

	void update() {
		if (game.locked) {
			tmpnear.normal.set(game.ViewVector);
			tmpnear.normal.multiply(0.01f);
			tmpnear.normal.add(game.PE.getPos());
			tmpnear.distance = game.ViewVector.DotProduct(tmpnear.normal);
			tmpnear.normal.set(game.ViewVector);
			return;
		}

		Vector ViewFrom = game.PE.getPos();
		
		// bottom
		ab.set(game.BottomViewVector);
		ab.multiply(vratio);
		ab.add(game.ViewVector);
		ab.CrossProduct2(game.RightViewVector);
		sides[3].normal.set(ab);
		sides[3].distance = sides[3].normal.DotProduct(ViewFrom);

		// left
		al.set(game.LeftViewVector);
		al.multiply(hratio);
		al.add(game.ViewVector);
		al.CrossProduct2(game.BottomViewVector);
		sides[0].normal.set(al);
		sides[0].distance = sides[0].normal.DotProduct(ViewFrom);

		// right
		ar.set(game.RightViewVector);
		ar.multiply(hratio);
		ar.add(game.ViewVector);
		ar.CrossProduct2(game.TopViewVector);
		sides[1].normal.set(ar);
		sides[1].distance = sides[1].normal.DotProduct(ViewFrom);


		// top
		at.set(game.TopViewVector);
		at.multiply(vratio);
		at.add(game.ViewVector);
		at.CrossProduct2(game.LeftViewVector);
		sides[2].normal.set(at);
		sides[2].distance = sides[2].normal.DotProduct(ViewFrom);

	}
}
