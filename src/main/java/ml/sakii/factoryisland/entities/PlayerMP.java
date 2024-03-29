package ml.sakii.factoryisland.entities;


import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.items.PlayerInventory;
import ml.sakii.factoryisland.net.Connection;

public class PlayerMP extends Entity {
	
	public Connection socket;
	public PlayerInventory inventory;
	public static final PlayerMP ServerPerson = null;//new PlayerMP();
	
	public PlayerMP(Vector ViewFrom, EAngle aim, String name, int health, long ID, GameEngine engine){ // createEntity hozza letre
		this(name,ViewFrom, aim.yaw, aim.pitch, health, new PlayerInventory(name, engine), null, ID, engine);
	}
	
	public PlayerMP(String username, Vector pos, float yaw, float pitch,int health, PlayerInventory inventory, Connection socket, long ID, GameEngine engine){
		super("PlayerMP", pos, new EAngle(yaw, pitch),username,health,20,ID,engine,AssetLibrary.playerFront, AssetLibrary.playerSide);
		this.inventory=inventory;
		this.socket=socket;
		Objects.add(new Text3D(name, ViewFrom.x, ViewFrom.y, ViewFrom.z));
	}
	
	/*public PlayerMP() {
		super("PlayerMP", new Vector(0,0,0), new EAngle(0, 0),"SERVER",Integer.MAX_VALUE,Integer.MAX_VALUE,0,Main.Engine,Surface.EMPTY,Surface.EMPTY);
	}*/

	

}
