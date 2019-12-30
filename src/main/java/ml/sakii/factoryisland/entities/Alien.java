package ml.sakii.factoryisland.entities;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;

public class Alien extends Entity {

	float[] fxy, fxy1, fx1y1, fx1y;
	public Vector target;
	public boolean locked=false;
	public Vector aim = new Vector();
	
	public Alien(Vector ViewFrom, EAngle aim, String name,int health,long ID, GameEngine engine){
		super("Alien",ViewFrom, aim, name,health,10,ID, engine, Main.alienFront, Main.alienSide);
		this.target=new Vector().set(ViewFrom);
	}

	
}
