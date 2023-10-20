package ml.sakii.factoryisland.blocks.components;

import java.util.HashMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class PowerComponent extends Component {
	
	public HashMap<BlockFace, Integer> powers = new HashMap<>();
	
	public PowerComponent(Block block) {
		super(block);
	}

	public int getPower()
	{
		int totalPower = 0;
		for(Entry<BlockFace, Integer> entry : powers.entrySet()) {
			totalPower += entry.getValue();
		}
		return totalPower;
	}

}
