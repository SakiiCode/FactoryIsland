package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.items.PlayerInventory;

public interface BlockInventoryInterface {
	//Inventory inv=new Inventory(Main.GAME==null ? null : Main.GAME.Engine);
	//public void setIndex(int index);
	public PlayerInventory getInv();
	public Block getBlock();
}
