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
	
	public PlayerMP(Vector ViewFrom, EAngle aim, String name,long ID, GameEngine engine){
		super("PlayerMP",ViewFrom, aim, name, Integer.MAX_VALUE,Integer.MAX_VALUE,ID,engine, Main.playerFront, Main.playerSide);
		showName=true;
		Objects.add(new Text3D(name, ViewFrom.x, ViewFrom.y, ViewFrom.z));
	}
	
	public PlayerMP(String username, Vector pos, float yaw, float pitch,int health, PlayerInventory inventory, Connection socket, long ID, GameEngine engine){
		super("PlayerMP", pos, new EAngle(yaw, pitch),username,health,20,ID,engine,Main.playerFront, Main.playerSide);
		this.inventory=inventory;
		this.socket=socket;
		
	}

	

	
	@Override
	public String toString() {
		return ((Text3D)Objects.get(6)).text + "," + ((Text3D)Objects.get(6)).x + "," + ((Text3D)Objects.get(6)).y + "," + ViewFrom;
	}
}
