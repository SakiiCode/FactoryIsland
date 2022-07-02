package ml.sakii.factoryisland;
import java.awt.Point;
import java.awt.geom.Point2D;



public class Vertex extends Vector{
	final Point proj=new Point();
	private final Vector Copy = new Vector();
	static final Vertex NULL = new Vertex(0, 0, 0);
	private final Point2D.Float spec = new Point2D.Float();
	
	public Vertex(float x, float y, float z/*, int u, int v*/) {
		super(x, y, z);
	}
	
	public Vertex(Vector vec/*, int u, int v*/) {
		this(vec.x, vec.y, vec.z);
	}
	
	public Vertex(Vertex v) {
		this(v.x, v.y, v.z/*, v.u, v.v*/);
	}
	
	void update(Game game) {

		// 2d koordináták kiszámítása
		game.convert3Dto2D(Copy.set(this), spec);
		proj.x = (int)spec.getX();
		proj.y = (int)spec.getY();
		
	}
	
	UVZ getUVZ(double[] uv, Game game) {
		// 1/z , u/z , v/z kiszámítása
		// z nem a kamera és a pont távolsága, hanem a kamera helyének, és a pontnak a kamera irányára vetített helyének távolsága
		// (egyszerû skalárszorzat)
		double z = Copy.set(this).substract(game.PE.getPos()).DotProduct(game.ViewVector);
		UVZ uvz=new UVZ();
		uvz.iz=1/z;
		uvz.uz=uv[0]/z;
		uvz.vz=uv[1]/z;
		uvz.ao=uv[2]/z;
		return uvz;
		
	}

	public boolean equals(float x, float y, float z) {
		return this.x == x && this.y==y && this.z == z;
	}
	
}
