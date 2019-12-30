package ml.sakii.factoryisland;

import ml.sakii.factoryisland.blocks.BlockFace;

public class Point3D {
	int x, y;
	public int z;
	
	public Point3D() {
		this(0, 0, 0);
	}
	
	public Point3D(int x, int y, int z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Point3D set(int x, int y, int z){
		this.x=x;
		this.y=y;
		this.z=z;
		return this;
	}
	public Point3D set(float x, float y, float z){
		this.x = (int)Math.floor(x);
		this.y = (int)Math.floor(y);
		this.z = (int)Math.floor(z);
		return this;
	}
	
	@Override
	public String toString() {
		return "x="+x+",y="+y+",z="+z;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Point3D))
			return false;
		Point3D other = (Point3D) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}

	public Point3D set(Point3D pos)
	{
		return set(pos.x, pos.y, pos.z);
	}
	
	public Point3D add(BlockFace face) {
		return set(x+face.direction[0], y+face.direction[1], z+face.direction[2]);
	}

}
