package ml.sakii.factoryisland.entities;


import java.lang.reflect.Constructor;
import java.util.ArrayList;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public class Entity{
	
	
	int health;
	int maxHealth;
	public ArrayList<Object3D> Objects = new ArrayList<>();
	public ArrayList<Vertex> Vertices = new ArrayList<>();
	public boolean showName = false;
	private Vector ViewFrom;
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

	public void move(float x, float y, float z) {
		ViewFrom.set(x, y, z);
		
		if(!(this instanceof PlayerEntity) && engine.client != null && engine.server != null) {
			engine.client.sendData("16,"+ID+","+ViewFrom);
		}
		
	}
	
	public void move(Vector v) {
		move(v.x, v.y, v.z);
	}
	
	public Vector getPos() {
		return ViewFrom;
	}

	@Override
	public String toString()
	{
		return "[" + ViewFrom + "," + className + "]\r\n";
	}
	
	
}
