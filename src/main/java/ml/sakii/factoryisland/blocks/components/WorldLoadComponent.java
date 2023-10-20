package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.blocks.Block;

public abstract class WorldLoadComponent extends Component{
	
	public WorldLoadComponent(Block block) {
		super(block);
	}

	public abstract void onLoad(Game game);
	
	@Override
	public String toString() {
		return "WorldLoad";
	}

}
