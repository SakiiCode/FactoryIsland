package ml.sakii.factoryisland.blocks;

import java.util.Random;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.items.ItemStack;

public class LeafBlock extends Block implements BreakListener{
	
	public static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.leaf);
	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	
	public LeafBlock(int x, int y, int z, GameEngine engine) {
		super("Leaf", x, y, z, engine);
		
	}

	@Override
	public ItemStack[] breaked(String username) {
		
		boolean giveSapling = new Random().nextInt(10) == 1;
		String type = giveSapling?"Sapling":"Leaf";
		return new ItemStack[] {new ItemStack(Main.Items.get(type),1)};
		
	}



}
