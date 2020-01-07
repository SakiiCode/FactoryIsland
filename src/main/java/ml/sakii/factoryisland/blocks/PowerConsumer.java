package ml.sakii.factoryisland.blocks;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;

public abstract class PowerConsumer extends Block implements PowerListener{
	
	public final HashMap<BlockFace, Integer> powers = new HashMap<>();
	
	public PowerConsumer(int x, int y, int z, GameEngine engine) //ModBlock és Nothing miatt
	{
		
		super(x, y, z,engine);
		init();
	}

	public PowerConsumer(String name, int x, int y, int z, GameEngine engine)
	{
		super(name, x, y, z,  engine);
		init();

	}
	
	public PowerConsumer(String name, int x, int y, int z, float xscale, float yscale, float zscale, GameEngine engine)
	{
		super(name, x, y, z, xscale, yscale, zscale, engine);
		init();

	}
	

	private void init() {

	}

	
	abstract void work();
	
	
	@Override
	public void addPower(int power, BlockFace relativeFrom) 
	{
		if(powers.get(relativeFrom) == null  || powers.get(relativeFrom) < power){
			if(getCharge()==0) {
				work();
			}
			powers.put(relativeFrom, power);
			setMetadata("powered", getCharge() + "", true);
		}
		

	}

	@Override
	public void removePower(BlockFace relativeFrom)
	{
		if(powers.containsKey(relativeFrom)){
			powers.remove(relativeFrom);
			setMetadata("powered", getCharge() + "", true);
		}

	}
	

	
	public int getCharge()
	{
		if (powers.isEmpty())
		{
			return 0;
		}
		return powersSorted().lastEntry().getKey();
	}
	
	TreeMap<Integer, BlockFace> powersSorted()
	{
		TreeMap<Integer, BlockFace> result = new TreeMap<>();
		for (Entry<BlockFace, Integer> entry : powers.entrySet())
		{
			result.put(entry.getValue(), entry.getKey());
		}

		return result;

	}
	

}
