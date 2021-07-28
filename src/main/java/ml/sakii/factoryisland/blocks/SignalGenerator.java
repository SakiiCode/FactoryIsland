package ml.sakii.factoryisland.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.items.ItemStack;

public interface SignalGenerator extends BreakListener, BlockInterface, TickListener{


	
	default void switchSignal(boolean on, BlockFace[] sides) {
		if(on) {
			setBlockMeta("active", "1", true);
		}else {
			setBlockMeta("active", "0", true);	
		}
		List<BlockFace> activeSides = Arrays.asList(sides);
		for(Entry<BlockFace, Block> e : getEngine().world.get6Blocks((Block)this, false).entrySet()){
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
	public default ItemStack[] breaked(String username) {
		switchSignal(false, BlockFace.values);
		return null;
	}
	
	@Override
	public default boolean tick(long tickCount) {
			refresh();
			return false;
		
	}
	
	abstract void refresh();

}
