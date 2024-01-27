package ml.sakii.factoryisland.blocks;


import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.BlockInventoryComponent;
import ml.sakii.factoryisland.blocks.components.BreakComponent;
import ml.sakii.factoryisland.blocks.components.InteractComponent;
import ml.sakii.factoryisland.items.BlockInventory;
import ml.sakii.factoryisland.items.ItemStack;
import ml.sakii.factoryisland.items.ItemType;

public class ChestModuleBlock extends Block implements BlockInventoryInterface{
	
	
	
	private static final Surface[] surfaces = Block.generateSurfaces(AssetLibrary.chestModule);
	
	private BreakComponent bc;
	private InteractComponent ic;
	private BlockInventoryComponent bic;
	
	public ChestModuleBlock(int x, int y, int z, GameEngine engine){
		super("ChestModule", x, y, z, engine);
		bc = new BreakComponent(this) {
			
			@Override
			public List<ItemStack> onBreak() {
				ArrayList<ItemStack> stacks = new ArrayList<>();
				
				for(Entry<ItemType, Integer> is : bic.getInv().items.entrySet()) {
					
					stacks.add(new ItemStack(is.getKey(),is.getValue()));
				}
				stacks.add(new ItemStack(Main.Items.get(ChestModuleBlock.this.name),1));
				return stacks;
			}
		};
		
		addComponent(bc);
		
		bic = new BlockInventoryComponent(this);
		
		addComponent(bic);
		
		ic = new InteractComponent(this) {
			@Override
			public void interact(BlockFace target) {
				Engine.game.openInventory(bic);	
			}
		};
		
		addComponent(ic);
	}

	@Override
	public BlockInventory getInv() {
		return bic.getInv();
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	
}
