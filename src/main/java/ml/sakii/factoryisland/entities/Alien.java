package ml.sakii.factoryisland.entities;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Vector;

public class Alien extends Entity {

	public Vector target;
	public boolean locked=false;
	public Vector aim = new Vector();
	
	public Alien(Vector ViewFrom, EAngle aim, String name,int health,long ID, GameEngine engine){
		super("Alien",ViewFrom, aim, name,health,10,ID, engine, AssetLibrary.alienFront, AssetLibrary.alienSide);
		this.target=new Vector().set(ViewFrom);
	}

	
}
