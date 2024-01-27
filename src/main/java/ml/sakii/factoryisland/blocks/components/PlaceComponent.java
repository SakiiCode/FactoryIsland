package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public abstract class PlaceComponent extends Component {

	public PlaceComponent(Block block) {
		super(block);
	}
	
	public abstract void placed(BlockFace selectedFace);

}
