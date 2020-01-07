package ml.sakii.factoryisland.api;

import java.util.ArrayList;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class API {
	
	public static GameEngine Engine;
	
	public static Block getBlockAt(int x, int y, int z) {
		return new Block(Engine.world.getBlockAt(x, y, z));
	}
	
	public static void setBlock(String name, int x, int y, int z) {
		Block b = new Block(name, x, y, z);
		Engine.world.addBlockNoReplace(b.b, true);
	}
	
	public static void deleteBlock(int x, int y, int z) {
		ml.sakii.factoryisland.blocks.Block b = Main.GAME.Engine.world.getBlockAt(x, y, z);
		if(b!=ml.sakii.factoryisland.blocks.Block.NOTHING) {
				Engine.world.destroyBlock(b, true);
		}
	}
	
	public static void setBlock(Block b) {
		Engine.world.addBlockNoReplace(b.b, true);
	}
	
	public static void teleport(float x, float y, float z) {
		if(!Main.headless) {
			Main.GAME.PE.move(x, y, z, true);
			Main.GAME.moved = true;
		}
	}
	
	public static Block[] getBlocksByName(String name) {
		ArrayList<Block> bl = new ArrayList<>();
		
		for(ml.sakii.factoryisland.blocks.Block b : Engine.world.getWhole(false)) {
			if(b.name.equals(name)) {
				bl.add(new Block(b));
			}
		}
		return bl.toArray(new Block[0]);
	}
	
	public static void addOne(String name) {
		if(!Main.headless) {
			Main.GAME.PE.inventory.add(Main.Items.get(name), 1, true);
		}
	}
	


}
