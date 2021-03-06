package ml.sakii.factoryisland.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.items.ItemStack;

public abstract class SignalGenerator extends Block implements BreakListener{


	
	public SignalGenerator(int x, int y, int z, GameEngine engine) //ModBlock és Nothing miatt
	{
		
		super(x, y, z,engine);
		init();
	}

	public SignalGenerator(String name, int x, int y, int z, GameEngine engine)
	{
		super(name, x, y, z,  engine);
		init();

	}
	
	public SignalGenerator(String name, int x, int y, int z, float xscale, float yscale, float zscale, GameEngine engine)
	{
		super(name, x, y, z, xscale, yscale, zscale, engine);
		init();

	}
	

	private void init() {
		BlockMeta.put("active", "0");

	}

	
	void switchSignal(boolean on, BlockFace[] sides) {
		if(on) {
			setMetadata("active", "1", true);
		}else {
			setMetadata("active", "0", true);	
		}
		List<BlockFace> activeSides = Arrays.asList(sides);
		for(Entry<BlockFace, Block> e : Engine.world.get6Blocks(this, false).entrySet()){
			BlockFace face = e.getKey();
			Block b = e.getValue();
			
			if(activeSides.contains(face) && b instanceof SignalListener sl) {
				
				if(on) {
					sl.addSignal(10, face.getOpposite());
				}else {
					sl.removeSignal(face.getOpposite());
				}
							
			}
		}
	}
	
	@Override
	public ItemStack[] breaked(String username) {
		switchSignal(false, BlockFace.values);
		return null;
	}

}
