package ml.sakii.factoryisland.blocks;


import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.BlockInventory;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class ChestModuleBlock extends Block implements InteractListener, BlockInventoryInterface, BreakListener, WorldGenListener{
	
	BlockInventory inv;
	public ChestModuleBlock(int x, int y, int z, GameEngine engine){
		super("ChestModule", x, y, z,  
				new Surface(Main.chestModule),
				new Surface(Main.chestModule),
				new Surface(Main.chestModule),
				new Surface(Main.chestModule),
				new Surface(Main.chestModule),
				new Surface(Main.chestModule),
				engine);
		inv = new BlockInventory(this,engine);
		//returnOnBreak=false;

	}

	@Override
	public void interact(BlockFace target) {
		Main.GAME.openInventory(this);
	}



	@Override
	public PlayerInventory getInv() {
		return inv;
	}

	@Override
	public Block getBlock() {
		return this;
	}

	@Override
	public boolean breaked(String username) {
		for(Entry<ItemType, Integer> is : inv.items.entrySet()) {
			for(Entity e : Engine.world.getAllEntities()) {
				if(e.name.equals(username) && e instanceof PlayerMP) {
					((PlayerMP)e).inventory.add(is.getKey(), is.getValue(), true);
					
				}
			}
		}
		return true;
	}

	@Override
	public void generateWorld() {
		inv.add(Main.Items.get("Stone"), 1, false);

	}
	
	
}
