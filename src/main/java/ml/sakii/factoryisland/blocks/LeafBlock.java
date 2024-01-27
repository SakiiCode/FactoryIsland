package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.BreakComponent;
import ml.sakii.factoryisland.items.ItemStack;

public class LeafBlock extends Block {

	private static final Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.leaf);

	private static final BlockDescriptor descriptor = new BlockDescriptor() {
		@Override
		public boolean isTransparent() {
			return true;
		}
		
		@Override
		public final boolean isFullBlock() {
			return false;
		}
	};
	
	private BreakComponent bc;

	public LeafBlock(int x, int y, int z, GameEngine engine) {
		super("Leaf", x, y, z, engine);
		bc = new BreakComponent(this) {
			
			@Override
			public List<ItemStack> onBreak() {
				boolean giveSapling = new Random().nextInt(10) == 1;
				String type = giveSapling ? "Sapling" : "Leaf";
				ArrayList<ItemStack> result = new ArrayList<>();
				result.add(new ItemStack(Main.Items.get(type), 1));
				return result;
			}
		};
		
		addComponent(bc);

	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor;
	}

}
