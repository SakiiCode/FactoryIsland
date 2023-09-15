package ml.sakii.factoryisland.entities;


import java.lang.reflect.Constructor;
import java.util.ArrayList;
import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Globals;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Model;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Util;
import ml.sakii.factoryisland.Vector;

public class Entity extends Model.FP {
	
	
	private int health;
	public final int maxHealth;
	public ArrayList<Object3D> Objects = new ArrayList<>();
	private ArrayList<Vector> Vertices = new ArrayList<>();
	public EAngle ViewAngle;
	public float GravityVelocity = 0f; // m/s
	public float JumpVelocity = 0f; // m/s
	public final Vector VerticalVector = new Vector(0, 0, 1);
	public boolean flying;
	public String className;
	public long ID;
	private GameEngine engine;
	private boolean moved=false;

	
	private float z0,z;
	private float[] fxy, fxy1, fx1y1, fx1y;
	
	public Entity(String className, Vector pos, EAngle aim, String name,int health, int maxHealth,long ID, GameEngine engine, Surface front, Surface side) {
		super(name, pos, engine);
		this.className=className;
		ViewAngle=aim;
		this.name = name;
		this.ID = ID;
		this.engine = engine;
		this.VerticalVector.z= ViewFrom.z >= 0 ? 1 : -1;
		
		this.maxHealth=maxHealth;
		this.health=health;
		
		init();
		
		Vector x1yz0 = new Vector(fx1y, z0);
		Vector x1y1z0 = new Vector(fx1y1, z0);
		Vector xyz0 = new Vector(fxy, z0);
		Vector xy1z0 = new Vector(fxy1, z0);
		
		Vector x1yz1 = new Vector(fx1y, z);
		Vector x1y1z1 = new Vector(fx1y1, z);
		Vector xyz1 = new Vector(fxy, z);
		Vector xy1z1 = new Vector(fxy1, z);
		
		Vertices.add(x1yz0);
		Vertices.add(x1y1z0);
		Vertices.add(xyz0);
		Vertices.add(xy1z0);
		
		Vertices.add(x1yz1);
		Vertices.add(x1y1z1);
		Vertices.add(xyz1);
		Vertices.add(xy1z1);

		
		
		
		//top
		Objects.add(new Polygon3D(new Vector[] {xy1z1, x1y1z1, x1yz1, xyz1},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side,this));
		//bottom
		Objects.add(new Polygon3D(new Vector[] {xyz0, x1yz0, x1y1z0, xy1z0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side,this));
		//back
		Objects.add(new Polygon3D(new Vector[] {xy1z1, xyz1, xyz0, xy1z0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side,this));
		//front
		Objects.add(new Polygon3D(new Vector[] {x1yz1, x1y1z1, x1y1z0, x1yz0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, front,this));
		//left
		Objects.add(new Polygon3D(new Vector[] {x1y1z1, xy1z1, xy1z0, x1y1z0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side,this));
		//right
		Objects.add(new Polygon3D(new Vector[] {xyz1, x1yz1, x1yz0, xyz0},new int[][] {{0,0},{0,0},{0,0},{0,0}}, side,this));
		
	}
	
	public void jump() {
		if (JumpVelocity == 0 && GravityVelocity == 0)
		{
			JumpVelocity = Globals.JumpForce;
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
			engine.client.sendEntityMove(ID, x, y, z, ViewAngle.yaw, ViewAngle.pitch);
		}else {
			ViewFrom.set(x, y, z);
			moved=true;
		}
		
	}
	
	@Override
	public Vector getPos() {
		return ViewFrom;
	}
	
	
	
	private void init() {
		float yaw2 = (float) Math.toRadians(ViewAngle.yaw);

		float x = ViewFrom.x;
		float y = ViewFrom.y;
		
		
		
		if(VerticalVector.z > 0) {
			z = ViewFrom.z;
			z0 = ViewFrom.z-1.7f;
		}else {
			
			z=ViewFrom.z+1.7f;
			z0=ViewFrom.z;
		}

		
		
		fxy = Util.rotateCoordinates(x-1f, y-0.5f, x, y, yaw2);
		fxy1 = Util.rotateCoordinates(x-1f, y+0.5f, x, y, yaw2);
		fx1y1 = Util.rotateCoordinates(x, y+0.5f, x, y, yaw2);
		fx1y = Util.rotateCoordinates(x,y-0.5f, x, y, yaw2);
		
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
			if(p instanceof Polygon3D poly) {

				poly.recalc();
				if(moved) {
					poly.recalcLightedColor();
					moved=false;
				}
				
			}else {
				((Text3D)p).setLocation(ViewFrom);				
			}
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
		return ID+","+ViewFrom+","+ViewAngle;
	}
	
	
	
	
}
