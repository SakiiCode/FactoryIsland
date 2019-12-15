package ml.sakii.factoryisland.entities;


import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.items.PlayerInventory;
import ml.sakii.factoryisland.net.Connection;

public class PlayerMP extends Entity {
	
	public Connection socket;
	public PlayerInventory inventory;
	
	public PlayerMP(Vector ViewFrom, EAngle aim, String name, int health, long ID, GameEngine engine){ // createEntity hozza letre
		//super("PlayerMP",ViewFrom, aim, name, health, 20,ID,engine, Main.playerFront, Main.playerSide);
		this(name,ViewFrom, aim.yaw, aim.pitch, health, new PlayerInventory(name, engine), null, ID, engine);
	}
	
	public PlayerMP(String username, Vector pos, float yaw, float pitch,int health, PlayerInventory inventory, Connection socket, long ID, GameEngine engine){
		super("PlayerMP", pos, new EAngle(yaw, pitch),username,health,20,ID,engine,Main.playerFront, Main.playerSide);
		this.inventory=inventory;
		this.socket=socket;
		showName=true;
		Objects.add(new Text3D(name, ViewFrom.x, ViewFrom.y, ViewFrom.z));
	}
	
	public PlayerMP(String username, GameEngine engine) { // atmeneti, parse elotti inicializalashoz TODO valszeg megkerulheto
		//super("PlayerMP",new Vector(19.5f, 19.5f, 15.0f), new EAngle(-135, 0), "",20,20, new Random().nextLong(), engine, Surface.EMPTY, Surface.EMPTY);
		this(username,new Vector(19.5f, 19.5f, 15.0f), -135,0,20, new PlayerInventory(username,engine),null,0, engine);
	}

	

}
