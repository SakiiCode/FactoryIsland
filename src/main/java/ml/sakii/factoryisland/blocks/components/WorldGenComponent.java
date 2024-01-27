package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;

public abstract class WorldGenComponent extends Component {

	public WorldGenComponent(Block block) {
		super(block);
	}
	
	public abstract void afterGen();
}
