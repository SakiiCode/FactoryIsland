package ml.sakii.factoryisland;


public class Vector {
	public float x, y, z;
	
	public static final Vector PLAYER = new Vector(0, 0, 1.7f);

	public static final Vector Z = new Vector(0, 0, 1);
	
	public Vector(){
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}
	
	public Vector(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(float[] xy, float z) {
		this.x = xy[0];
		this.y = xy[1];
		this.z = z;
	}
	
	public Vector add(Vector V){
		this.set(x+V.x,y+V.y,z+V.z);
		return this;
	}
	
	public Vector substract(Vector V) {
		this.set(x-V.x,y-V.y,z-V.z);
		return this;
	}

	
	public Vector CrossProduct(Vector V)
	{
		this.set(
				y * V.z - z * V.y,
				z * V.x - x * V.z,
				x * V.y - y * V.x);
		normalize();
		return this;
		
	}
	
	public Vector CrossProduct2(Vector v) {
		this.set(
				v.y * z - v.z * y,
				v.z * x - v.x * z,
				v.x * y - v.y * x);
		normalize();
		//CrossVector.normalize();
		//return CrossVector;
		return this;
	}
	
	public Vector multiply(float value){
		x *=value;
		y *=value;
		z *=value;
		return this;
		//return new Vector(x*value,y*value,z*value);
	}
	
	public float distance(Vector V) {
		float x2 = V.x-x;
		float y2 = V.y-y;
		float z2 = V.z-z;
		return (float) Math.sqrt(x2*x2+y2*y2+z2*z2);
		//return  V.cpy().multiply(-1).add(this).getLength();
	}
	
	/*public Vector cpy() {
		return new Vector(x, y, z);
	}*/
	
	public Vector to(Vector v) {
		return new Vector(v.x-x, v.y-y, v.z-z);
	}
	
	/*public float DotProduct(float x, float y, float z){
		return this.x*x+this.y*y+this.z*z;
	}*/
	
	public float[] toFloatArray() {
		return new float[] {x, y, z};
	}
	
	public float DotProduct(Vector V){
		return x*V.x+y*V.y+z*V.z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vector))
			return false;
		Vector other = (Vector) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}
	
	public float getLength(){
		
		return (float) Math.sqrt(x*x+y*y+z*z);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}
	

	
	public Vector normalize(){
		float length = (float) Math.sqrt(x*x+y*y+z*z);

		if(length != 0){
			x=x/length;
			y=y/length;
			z=z/length;
		}
		

		return this;
	}

	/*public Vector opposite(){
		return new Vector(-x, -y, -z);
	}*/

	public Vector set(float x, float y, float z){
		this.x=x;
		this.y=y;
		this.z=z;
		return this;
	}
	
	public Vector set(float[] pos){
		this.x=pos[0];
		this.y=pos[1];
		this.z=pos[2];
		return this;
	}
	
	public Vector set(float[] xy, float z){
		this.x=xy[0];
		this.y=xy[1];
		this.z=z;
		return this;
	}
	
	public Vector set(Vector V){
		this.x=V.x;
		this.y=V.y;
		this.z=V.z;
		return this;
	}
	
	@Override
	public String toString(){
		return x+","+y+","+z;
		
	}
	
	public static Vector parseVector(String str) {
		String[] arr = str.split(",");
		
		return new Vector(Float.parseFloat(arr[0]), Float.parseFloat(arr[1]), Float.parseFloat(arr[2]));
		
	}

	public Vector set(Point3D V) {
		this.x=V.x;
		this.y=V.y;
		this.z=V.z;
		return this;
	}
	
	
}
