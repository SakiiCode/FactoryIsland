package ml.sakii.factoryisland.entities;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.TreeSet;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public class Entity{
	
	
	int health;
	public final int maxHealth;
	public ArrayList<Object3D> Objects = new ArrayList<>();
	public ArrayList<Vertex> Vertices = new ArrayList<>();
	public boolean showName = false;
	public Vector ViewFrom;
	public EAngle ViewAngle;
	public final float GravityAcceleration = 9.81f; // m/s^2
	public float GravityVelocity = 0f; // m/s
	public final float JumpForce = 7f;
	public float JumpVelocity = 0f; // m/s
	public final Vector VerticalVector = new Vector(0, 0, 1);
	public boolean flying;
	public String className;
	public String name;
	public long ID;
	private GameEngine engine;
	Vector tmpVector = new Vector();
	
	//többszálasítás miatt ide kellett áthozni a getBlockUnderPlayer átmeneti értékeit 
	public Point3D tmpPoint = new Point3D();
	public Point3D feetPoint = new Point3D();
	public TreeSet<Point3D> playerColumn = new TreeSet<>((arg0, arg1) -> Integer.compare(arg0.z, arg1.z));

	
	public float yaw, yaw2, z0,z;
	float[] fxy, fxy1, fx1y1, fx1y;
	
	Entity(String className, Vector pos, EAngle aim, String name,int health, int maxHealth,long ID, GameEngine engine, Surface front, Surface side) {
		
		this.className=className;
		ViewFrom=pos;
		ViewAngle=aim;
		this.name = name;
		this.ID = ID;
		this.engine = engine;
		this.VerticalVector.z= ViewFrom.z >= 0 ? 1 : -1;
		
		this.maxHealth=maxHealth;
		this.health=health;
		
		init();
		
		Vertex x1yz0 = new Vertex(new Vector(fx1y, z0));
		Vertex x1y1z0 = new Vertex(new Vector(fx1y1, z0));
		Vertex xyz0 = new Vertex(new Vector(fxy, z0));
		Vertex xy1z0 = new Vertex(new Vector(fxy1, z0));
		
		Vertex x1yz1 = new Vertex(new Vector(fx1y, z));
		Vertex x1y1z1 = new Vertex(new Vector(fx1y1, z));
		Vertex xyz1 = new Vertex(new Vector(fxy, z));
		Vertex xy1z1 = new Vertex(new Vector(fxy1, z));
		
		Vertices.add(x1yz0);
		Vertices.add(x1y1z0);
		Vertices.add(xyz0);
		Vertices.add(xy1z0);
		
		Vertices.add(x1yz1);
		Vertices.add(x1y1z1);
		Vertices.add(xyz1);
		Vertices.add(xy1z1);

		
		
		
		//top TODO nem j�?
		Objects.add(new Polygon3D(new Vertex[] {xy1z1, x1y1z1, x1yz1, xyz1},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side));
		//bottom TODO nem j�?
		Objects.add(new Polygon3D(new Vertex[] {xyz0, x1yz0, xy1z0, xy1z0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side));
		//left
		Objects.add(new Polygon3D(new Vertex[] {xy1z1, xyz1, xyz0, xy1z0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side));
		//right
		Objects.add(new Polygon3D(new Vertex[] {x1yz1, x1y1z1, x1y1z0, x1yz0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side));
		//front
		Objects.add(new Polygon3D(new Vertex[] {x1y1z1, xy1z1, xy1z0, x1y1z0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, front));
		//back
		Objects.add(new Polygon3D(new Vertex[] {xyz1, x1yz1, x1yz0, xyz0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side));
		
	}
	
	public void jump() {
		if (JumpVelocity == 0 && GravityVelocity == 0)
		{
			JumpVelocity = JumpForce;
		}
	}
	
	public void fly(boolean on)
	{
		flying = on;
		if (on)
		{
			JumpVelocity = 0;
			GravityVelocity = 0;
		}

	}

	public static Entity createEntity(String className, Vector pos, EAngle aim, String name,int health, long ID, GameEngine engine) {
		
		try{
			Class<?> entityClass = Class.forName("ml.sakii.factoryisland.entities."+className);
			if(!entityClass.getName().equals(Entity.class.getName())){
				if(Entity.class.isAssignableFrom(entityClass)){
					Constructor<?> ctor = entityClass.getConstructor(Vector.class, EAngle.class, String.class, int.class, long.class, GameEngine.class);
					Entity object = (Entity)(ctor.newInstance(new Object[] { pos, aim, name,health, ID, engine }));
					return object;
				}
				Main.err("Could create entity: "+className+" is not an instance of " + Entity.class.getName());
				return null;
			}
			Main.err("Could create entity: Do not use ml.sakii.factoryisland.entities.Entity!");
			return null;
		
		
		
		}catch(Exception e){
			Main.err("Could not create entity: "+ className);
			e.printStackTrace();
			return null;
		}
		
	}

	public void move(float x, float y, float z, boolean resend) {
		if(resend && engine.client != null) { // PlayerEntity-NÉL MINDIG FALSE A RESEND
			//if(!(this instanceof PlayerEntity)) {// && engine.client != null && engine.server != null) {
				engine.client.sendEntityMove(this);
			//}
			
		}else {
			ViewFrom.set(x, y, z);
		}
		
	}
	
	public void move(Vector v, boolean resend) {
		move(v.x, v.y, v.z, resend);
	}
	
	public Vector getPos() {
		return ViewFrom;
	}
	
	static float[] rotateCoordinates(float x, float y,float centerX, float centerY, float angle){
		float newX = (float)(   ((x-centerX)*Math.cos(angle) - (y-centerY)*Math.sin(angle)) + centerX   );
		float newY = (float)(   ((x-centerX)*Math.sin(angle) + (y-centerY)*Math.cos(angle)) + centerY   );
		return new float[]{newX, newY};
	}
	
	private void init() {
		float yaw2 = -ViewAngle.yaw;//(float) (-ViewAngle.yaw +Math.PI/2);

		float x = ViewFrom.x;
		float y = ViewFrom.y;
		
		
		
		if(VerticalVector.z==1) {
			z = ViewFrom.z;
			z0 = ViewFrom.z-1.7f;
		}else {
			
			z=ViewFrom.z+1.7f;
			z0=ViewFrom.z;
		}

		
		
		fxy = rotateCoordinates(x-0.5f, y-1f, x, y, yaw2);
		fxy1 = rotateCoordinates(x-0.5f, y, x, y, yaw2);
		fx1y1 = rotateCoordinates(x+0.5f, y, x, y, yaw2);
		fx1y = rotateCoordinates(x+0.5f,y-1f, x, y, yaw2);
		
	}
	
	public void update(){

		init();
		
		
		
		Vertices.get(0).set(fx1y, z0);
		Vertices.get(1).set(fx1y1, z0);
		Vertices.get(2).set(fxy, z0);
		Vertices.get(3).set(fxy1, z0);
		Vertices.get(4).set(fx1y, z);
		Vertices.get(5).set(fx1y1, z);
		Vertices.get(6).set(fxy, z);
		Vertices.get(7).set(fxy1, z);
		
		for(Object3D p : Objects) {
			if(p instanceof Polygon3D) {

				((Polygon3D) p).recalc(tmpVector);
			}else {
				((Text3D)p).location.set(ViewFrom);				
			}
		}

		
	}
	
	// true ha tulelte
	public boolean hurt(int points, boolean resend) {
		if(resend && engine.client != null) { // PlayerEntity-NÉL MINDIG FALSE A RESEND TODO itt is?
			engine.client.sendEntityHurt(this.ID, points);
			return true;
		}else {
			health = Math.min(Math.max(health-points,0), maxHealth);
			return health!=0;
		}
	}
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health=health;
	}

	@Override
	public String toString()
	{
		//return "[" + ViewFrom + "," + className + "]\r\n";
		return ID+","+ViewFrom;
	}
	
	
	
	
}
