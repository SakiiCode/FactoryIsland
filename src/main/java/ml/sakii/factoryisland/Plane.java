package ml.sakii.factoryisland;

public class Plane {
	Vector normal;
	float distance;
	
	public Plane(Vector a, Vector b, Vector c){
		
		Vector q = new Vector().set(b).substract(a);
		Vector v = new Vector().set(b).substract(c);
		normal = q.CrossProduct(v);
		
		/*
		Vector q = new Vector(b.x - a.x, b.y - a.y, b.z - a.z);
		Vector v = new Vector(b.x - c.x, b.y - c.y, b.z - c.z);
		normal = q.CrossProduct(v);
		*/		
		
		distance = normal.DotProduct(a);
		
		
	}
	
	public Plane(Vector normal, Vector point){
		normal.normalize();
		distance = normal.DotProduct(point);
		this.normal=normal;

	}
	
	public Plane(Vector normal, float distance){
		normal.normalize();
		this.distance = distance;
		this.normal=normal;

	}

	public Plane() {
		normal = new Vector();
		distance = 0.0f;
	}
}
