package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;

public abstract class TickUpdateComponent extends Component {
	
	public int refreshRate;

	public TickUpdateComponent(Block block, int refreshRate) {
		super(block);
		this.refreshRate=refreshRate;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return true if keep in update queue
	 */
	
	public abstract boolean onTick();
	
	@Override
	public String toString() {
		return "TickUpdate("+refreshRate+")";
	}
	
	

}
