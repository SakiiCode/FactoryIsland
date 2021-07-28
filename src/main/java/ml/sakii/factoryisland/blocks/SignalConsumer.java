package ml.sakii.factoryisland.blocks;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

public interface SignalConsumer extends SignalListener, BlockInterface{
	
	public HashMap<BlockFace, Integer> getSignals();
	
	abstract void work();
	
	
	@Override
	public default void addSignal(int signal, BlockFace relativeFrom) 
	{
		if(getSignals().get(relativeFrom) == null  || getSignals().get(relativeFrom) < signal){
			if(getCharge()==0) {
				work();
			}
			getSignals().put(relativeFrom, signal);
			setBlockMeta("signalLevel", getCharge() + "", true);
		}
		

	}

	@Override
	public default void removeSignal(BlockFace relativeFrom)
	{
		if(getSignals().containsKey(relativeFrom)){
			getSignals().remove(relativeFrom);
			setBlockMeta("signalLevel", getCharge() + "", true);
		}

	}
	

	
	public default int getCharge()
	{
		if (getSignals().isEmpty())
		{
			return 0;
		}
		return signalsSorted().lastEntry().getKey();
	}
	
	default TreeMap<Integer, BlockFace> signalsSorted()
	{
		TreeMap<Integer, BlockFace> result = new TreeMap<>();
		for (Entry<BlockFace, Integer> entry : getSignals().entrySet())
		{
			result.put(entry.getValue(), entry.getKey());
		}

		return result;

	}
	

}
