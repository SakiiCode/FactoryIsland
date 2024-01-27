package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;

public abstract class DayNightComponent extends Component {

	public DayNightComponent(Block block) {
		super(block);
	}
	
	public abstract void onDay();
	public abstract void onNight();
	
	public Block getBlock() {
		return block;
	}

}
