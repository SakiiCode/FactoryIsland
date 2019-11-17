package ml.sakii.factoryisland;
import java.awt.Point;
import java.awt.geom.Point2D;



public class Vertex {



	public final Vector pos;
	//final UVZ uvz = new UVZ();
	//int u, v;
	final Point proj;
	private final Vector ViewToPoint = new Vector();
	static final Vertex NULL = new Vertex(0, 0, 0);
	private final Point2D.Float spec = new Point2D.Float();
	
	public Vertex(float x, float y, float z/*, int u, int v*/) {
		this(new Vector(x,y,z));
	}
	
	public Vertex(Vector vec/*, int u, int v*/) {
		this.pos=vec; // a biztons�g kedv��rt objektumot m�solunk
		/*this.u=u;
		this.v = v;*/
		proj = new Point();
		//update();
	}
	
	public Vertex(Vertex v) {
		this(v.pos.x, v.pos.y, v.pos.z/*, v.u, v.v*/);
	}
	
	public void update() {
		// 1/z , u/z , v/z kisz�m�t�sa
		// z nem a kamera �s a pont t�vols�ga, hanem a kamera hely�nek, �s a pontnak a kamera ir�ny�ra vet�tett hely�nek t�vols�ga
		// (egyszer� skal�rszorzat)
		/*if(Config.useTextures) { 
			 
			double z = ViewToPoint.set(pos).substract(Main.GAME.PE.getPos()).DotProduct(Main.GAME.ViewVector);
			//ViewToPoint;//= Main.GAME.PE.ViewFrom.to(pos);//pos.add(Main.GAME.PE.ViewFrom.multiply(-1)); 
			 //= ViewToPoint;
			uvz.iz=1/z;
			uvz.uz=u/z;
			uvz.vz=v/z;
		}*/
		
		// 2d koordin�t�k kisz�m�t�sa
		//ViewToPoint.set(pos);
		Main.GAME.convert3Dto2D(new Vector().set(pos), spec);
		proj.x = (int)spec.getX();
		proj.y = (int)spec.getY();
		
		
	}
	
	public UVZ getUVZ(int[] uv) {
		double z = new Vector().set(pos).substract(Main.GAME.PE.getPos()).DotProduct(Main.GAME.ViewVector);
		//ViewToPoint;//= Main.GAME.PE.ViewFrom.to(pos);//pos.add(Main.GAME.PE.ViewFrom.multiply(-1)); 
		 //= ViewToPoint;
		UVZ uvz=new UVZ();
		uvz.iz=1/z;
		uvz.uz=uv[0]*1.0/z;
		uvz.vz=uv[1]*1.0/z;
		return uvz;
		
	}
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vertex other = (Vertex) obj;
		if (pos == null)
		{
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		return true;
	}

	public static int[] setInterp(Vector p1, Vector pos, Vector p2, int[]uv1,int[]uv2, Vertex target) {
		double distanceratio = p1.distance(pos) / p1.distance(p2);
		int u = (int) Util.interp(0, 1, distanceratio, uv1[0], uv2[0]);
		int v = (int) Util.interp(0, 1, distanceratio, uv1[1], uv2[1]);
		//Vector pos2 = v2.pos.add(v1.pos.multiply(-1)).multiply((float) (1/distanceratio));
		target.set(pos/*, u, v*/);
		/*uvTarget[0]=u;
		uvTarget[1]=v;*/
		return new int[] {u,v};
		//return new Vertex(new Vector().set(pos));
	}
	
	public void set(Vector pos/*, int u, int v*/) {
		this.pos.set(pos);
		/*this.u=u;
		this.v=v;*/
	}
	
	public void set(Vertex v) {
		this.pos.set(v.pos);
		/*this.u=v.u;
		this.v=v.v;*/
	}
	
	@Override
	public String toString() {
		return "V(" + pos/* + ","+u+","+v*/+")";
	}

}
