package ml.sakii.factoryisland;

import java.awt.geom.Point2D;

public class Vector2D extends Point2D.Float{

	private static final long serialVersionUID = 4303230416601452962L;
	
	public Vector2D add(Vector V){
		this.set(x+V.x,y+V.y);
		return this;
	}
	
	public Vector2D add(Point2D.Float V){
		this.set(x+V.x,y+V.y);
		return this;
	}
	
	public Vector2D substract(Vector V) {
		this.set(x-V.x,y-V.y);
		return this;
	}
	
	public Vector2D substract(Point2D.Float V) {
		this.set(x-V.x,y-V.y);
		return this;
	}
	
	public Vector2D multiply(float value){
		x *=value;
		y *=value;
		return this;
	}
	
	public Vector2D normalize(){
		float length = (float) Math.sqrt(x*x+y*y);

		if(length != 0){
			x=x/length;
			y=y/length;
		}
		

		return this;
	}
	
	public Vector2D set(float x, float y){
		this.x=x;
		this.y=y;
		return this;
	}
	
	public Vector2D set(Point2D.Float v){
		this.x=v.x;
		this.y=v.y;
		return this;
	}
	
	public float DotProduct(Vector2D V){
		return x*V.x+y*V.y;
	}
	

}
