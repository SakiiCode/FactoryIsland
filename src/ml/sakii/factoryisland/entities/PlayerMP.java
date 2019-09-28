package ml.sakii.factoryisland.entities;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Vector;

public class PlayerMP extends Entity {
	
	public PlayerMP(Vector ViewFrom, EAngle aim, String name,long ID, GameEngine engine){
		super("PlayerMP",ViewFrom, aim, name,ID, engine, Main.playerFront, Main.playerSide);
		showName=true;
		Objects.add(new Text3D(name, ViewFrom.x, ViewFrom.y, ViewFrom.z));
	}
	

	

	
	@Override
	public String toString() {
		return ((Text3D)Objects.get(6)).text + "," + ((Text3D)Objects.get(6)).x + "," + ((Text3D)Objects.get(6)).y + "," + ViewFrom;
	}
}
