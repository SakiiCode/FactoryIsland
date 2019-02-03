package ml.sakii.factoryisland.blocks;


import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.items.BlockInventory;
import ml.sakii.factoryisland.items.ItemStack;
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
		//Engine.Inv.addOne(Main.Items.get("ChestModule"));
		for(ItemStack is : inv.items) {
			if(Engine.client==null) {
				Engine.Inv.add(is.kind, is.amount, true);
			}else {
				Engine.client.sendData("10,"+username+","+is.kind.name+","+is.amount);
			}
		}
		return true;
	}

	@Override
	public void generateWorld() {
		inv.add(Main.Items.get("Stone"), 1, false);

	}
	
	
}
