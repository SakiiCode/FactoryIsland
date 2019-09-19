package ml.sakii.factoryisland.entities;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.TreeSet;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public class Entity{
	
	
	int health;
	int maxHealth;
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

	
	Entity(String className, Vector pos, EAngle aim, String name,long ID, GameEngine engine) {
		this.className=className;
		ViewFrom=pos;
		ViewAngle=aim;
		this.name = name;
		this.ID = ID;
		this.engine = engine;
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

	public static Entity createEntity(String className, Vector pos, EAngle aim, String name, long ID, GameEngine engine) {
		
		try{
			Class<?> entityClass = Class.forName("ml.sakii.factoryisland.entities."+className);
			if(!entityClass.getName().equals(Entity.class.getName())){
				if(Entity.class.isAssignableFrom(entityClass)){
					Constructor<?> ctor = entityClass.getConstructor(Vector.class, EAngle.class, String.class, long.class, GameEngine.class);
					Entity object = (Entity)(ctor.newInstance(new Object[] { pos, aim, name, ID, engine }));
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
				engine.client.sendData("16,"+ID+","+x+","+y+","+z); 
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
	
	public void update() {
		
	}

	@Override
	public String toString()
	{
		//return "[" + ViewFrom + "," + className + "]\r\n";
		return ID+","+ViewFrom;
	}
	
	
	
	
}
