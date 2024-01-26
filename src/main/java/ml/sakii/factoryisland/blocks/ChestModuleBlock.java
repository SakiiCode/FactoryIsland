package ml.sakii.factoryisland.blocks;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.BreakComponent;
import ml.sakii.factoryisland.items.BlockInventory;
import ml.sakii.factoryisland.items.ItemStack;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class ChestModuleBlock extends Block implements InteractListener, BlockInventoryInterface, BreakListener{
	
	private BlockInventory inv;
	
	private static final Surface[] surfaces = Block.generateSurfaces(AssetLibrary.chestModule);
	
	private BreakComponent bc;
	
	public ChestModuleBlock(int x, int y, int z, GameEngine engine){
		super("ChestModule", x, y, z, engine);
		inv = new BlockInventory(engine);
		bc = new BreakComponent(this) {
			
			@Override
			public List<ItemStack> onBreak() {
				ArrayList<ItemStack> stacks = new ArrayList<>();
				
				for(Entry<ItemType, Integer> is : inv.items.entrySet()) {
					
					stacks.add(new ItemStack(is.getKey(),is.getValue()));
				}
				stacks.add(new ItemStack(Main.Items.get(ChestModuleBlock.this.name),1));
				return stacks;
			}
		};
		
		addComponent(bc);
	}

	@Override
	public void interact(BlockFace target) {
		Engine.game.openInventory(this);
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
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	
}
