package ml.sakii.factoryisland.blocks;


import java.util.ArrayList;
import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.BlockInventory;
import ml.sakii.factoryisland.items.ItemStack;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class ChestModuleBlock extends Block implements InteractListener, BlockInventoryInterface, BreakListener, WorldGenListener{
	
	BlockInventory inv;
	
	public static Surface[] surfaces = new Surface[] {
			new Surface(Main.chestModule),
			new Surface(Main.chestModule),
			new Surface(Main.chestModule),
			new Surface(Main.chestModule),
			new Surface(Main.chestModule),
			new Surface(Main.chestModule)};
	
	
	public ChestModuleBlock(int x, int y, int z, GameEngine engine){
		super("ChestModule", x, y, z, engine);
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
	public ItemStack[] breaked(String username) {
		
		ArrayList<ItemStack> stacks = new ArrayList<>();
		
		for(Entry<ItemType, Integer> is : inv.items.entrySet()) {
			
			stacks.add(new ItemStack(is.getKey(),is.getValue()));
		}
		stacks.add(new ItemStack(Main.Items.get(this.name),1));
		return stacks.toArray(new ItemStack[0]);
	}

	@Override
	public void generateWorld() {
		inv.add(Main.Items.get("Stone"), 1, false);

	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	
}
