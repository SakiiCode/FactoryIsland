package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;

public abstract class TickUpdateComponent extends Component {

	public TickUpdateComponent(Block block) {
		super(block);
		// TODO Auto-generated constructor stub
	}
	
	public abstract void onTick();
	
	

}
