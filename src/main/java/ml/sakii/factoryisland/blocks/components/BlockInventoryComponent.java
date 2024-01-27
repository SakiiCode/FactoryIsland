package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.items.BlockInventory;

public class BlockInventoryComponent extends Component{
	
	private BlockInventory inv;
	
	public BlockInventoryComponent(Block block) {
		super(block);
		inv = new BlockInventory(block.Engine);
	}

	public BlockInventory getInv() {
		return inv;
	}

	public void setInv(BlockInventory inv) {
		this.inv = inv;
	}
	
	public Block getBlock() {
		return block;
	}
}
