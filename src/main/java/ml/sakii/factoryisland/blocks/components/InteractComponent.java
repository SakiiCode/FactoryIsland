package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public abstract class InteractComponent extends Component {

	public InteractComponent(Block block) {
		super(block);
	}
	
	public abstract void interact(BlockFace target);

}
