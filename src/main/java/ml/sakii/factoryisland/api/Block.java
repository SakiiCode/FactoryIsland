package ml.sakii.factoryisland.api;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.blocks.TickListener;

public class Block{

	public int x, y, z;
	public String name;
	ml.sakii.factoryisland.blocks.Block b;
	
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
		if(Main.headless) {
			b = Main.Engine.createBlockByName(name, x, y, z);
		}else {
			b = Main.GAME.Engine.createBlockByName(name, x, y, z);
		}
		
	}
	
	@Override
	public String toString(){
		return name + "," + x + "," + y + "," + z;
	}
	
	public String getName(){
		return name;
	}
	
	public String getMetadata(String key) {
		return b.BlockMeta.get(key);
	}
	
	public void setMetadata(String key, String value) {
		//b.BlockMeta.put(key, value);
		b.setMetadata(key, value, true);
	}
	
	public void update() {
		if(b instanceof TickListener) {
			if(Main.headless) {
				Main.Engine.TickableBlocks.add(b.pos);
			}else {
				Main.GAME.Engine.TickableBlocks.add(b.pos);
			}
		}
	}
	
	/*public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getZ() {
		return z;
	}*/
	
	
	

}
