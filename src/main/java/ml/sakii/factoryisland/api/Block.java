package ml.sakii.factoryisland.api;

import ml.sakii.factoryisland.GameEngine;

public class Block{

	public int x, y, z;
	public String name;
	ml.sakii.factoryisland.blocks.Block b;
	public static GameEngine Engine;
	
	Block(ml.sakii.factoryisland.blocks.Block b) {
		x=b.x;
		y=b.y;
		z=b.z;
		name = b.name;
		this.b = b;
	}
	
	Block(String name, int x, int y, int z) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.name = name;
		this.b = Engine.createBlockByName(name, x, y, z);
	}
	
	@Override
	public String toString(){
		return name + "," + x + "," + y + "," + z;
	}
	
	public String getName(){
		return name;
	}
	
	public String getMetadata(String key) {
		return b.getMetadata(key);
	}
	
	public void setMetadata(String key, String value) {
		b.setMetadata(key, value, true);
	}
	
	public void update() {
		Engine.TickableBlocks.add(b.pos);
	}
	
	
	

}
