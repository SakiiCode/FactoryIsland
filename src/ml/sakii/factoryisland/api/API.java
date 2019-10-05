package ml.sakii.factoryisland.api;

import java.util.ArrayList;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.Main;

public class API {
	
	public static Block getBlockAt(int x, int y, int z) {
		return new Block(Main.GAME.Engine.world.getBlockAt(x, y, z));
	}
	
	public static void setBlock(String name, int x, int y, int z) {
		Block b = new Block(name, x, y, z);
		Main.GAME.Engine.world.addBlockNoReplace(b.b, true);//TODO itt replace volt
	}
	
	public static void deleteBlock(int x, int y, int z) {
		ml.sakii.factoryisland.blocks.Block b = Main.GAME.Engine.world.getBlockAt(x, y, z);
		if(b!=ml.sakii.factoryisland.blocks.Block.NOTHING) {
			if (Main.GAME.Engine.client == null) {
				Main.GAME.Engine.world.destroyBlock(b, true);
			} else {
				Main.GAME.Engine.client.sendData(("06," + Config.username + "," + x + "," + y + "," + z));
			}
		}
	}
	
	public static void setBlock(Block b) {
		Main.GAME.Engine.world.addBlockNoReplace(b.b, true);//TODO itt replace volt
	}
	
	public static void teleport(float x, float y, float z) {
		Main.GAME.PE.move(x, y, z, true);
		Main.GAME.moved = true;
	}
	
	public static Block[] getBlocksByName(String name) {
		ArrayList<Block> bl = new ArrayList<>();
		for(ml.sakii.factoryisland.blocks.Block b : Main.GAME.Engine.world.getWhole(false)) {
			if(b.name.equals(name)) {
				bl.add(new Block(b));
			}
		}
		return bl.toArray(new Block[0]);
	}
	
	public static void addOne(String name) {
		Main.GAME.Engine.Inv.add(Main.Items.get(name), 1, true);
	}
	
	/*public static void log(String message) {
		Main.log(message);
	}*/
	

}
