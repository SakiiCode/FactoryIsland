package ml.sakii.factoryisland.blocks;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;

public abstract class SignalConsumer extends Block implements SignalListener{
	
	public final HashMap<BlockFace, Integer> signals = new HashMap<>();
	
	public SignalConsumer(int x, int y, int z, GameEngine engine) //ModBlock és Nothing miatt
	{
		
		super(x, y, z,engine);
		init();
	}

	public SignalConsumer(String name, int x, int y, int z, GameEngine engine)
	{
		super(name, x, y, z,  engine);
		init();

	}
	
	public SignalConsumer(String name, int x, int y, int z, float xscale, float yscale, float zscale, GameEngine engine)
	{
		super(name, x, y, z, xscale, yscale, zscale, engine);
		init();

	}
	

	private void init() {

	}

	
	abstract void work();
	
	
	@Override
	public void addSignal(int signal, BlockFace relativeFrom) 
	{
		if(signals.get(relativeFrom) == null  || signals.get(relativeFrom) < signal){
			if(getCharge()==0) {
				work();
			}
			signals.put(relativeFrom, signal);
			setMetadata("signalLevel", getCharge() + "", true);
		}
		

	}

	@Override
	public void removeSignal(BlockFace relativeFrom)
	{
		if(signals.containsKey(relativeFrom)){
			signals.remove(relativeFrom);
			setMetadata("signalLevel", getCharge() + "", true);
		}

	}
	

	
	public int getCharge()
	{
		if (signals.isEmpty())
		{
			return 0;
		}
		return signalsSorted().lastEntry().getKey();
	}
	
	TreeMap<Integer, BlockFace> signalsSorted()
	{
		TreeMap<Integer, BlockFace> result = new TreeMap<>();
		for (Entry<BlockFace, Integer> entry : signals.entrySet())
		{
			result.put(entry.getValue(), entry.getKey());
		}

		return result;

	}
	

}
