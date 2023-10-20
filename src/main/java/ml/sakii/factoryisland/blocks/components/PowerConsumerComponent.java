package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class PowerConsumerComponent extends PowerComponent {

	public PowerConsumerComponent(Block block) {
		super(block);
	}
	
	public void addPower(int intensity, BlockFace source) {
		if (intensity > 0) {
			powers.put(source, intensity);
		} else {
			powers.remove(source);
		}
		updatePower();
	}
	
	public void updatePower() {
		
	}

}
