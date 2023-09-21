package ml.sakii.factoryisland.blocks.components;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class SignalComponent extends Component{


	public HashMap<BlockFace, Integer> signals = new HashMap<>();


	public SignalComponent(Block block) {
		super(block);
	}
	
	public int getCharge()
	{
		if (signals.isEmpty())
		{
			return 0;
		}
		return powersSorted().lastEntry().getKey();
	}
	
	private TreeMap<Integer, BlockFace> powersSorted()
	{
		TreeMap<Integer, BlockFace> result = new TreeMap<>();
		for (Entry<BlockFace, Integer> entry : signals.entrySet())
		{
			result.put(entry.getValue(), entry.getKey());
		}

		return result;

	}
}
