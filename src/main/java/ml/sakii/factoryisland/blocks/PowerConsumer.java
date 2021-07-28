package ml.sakii.factoryisland.blocks;

import java.util.HashMap;
import java.util.LinkedList;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.items.ItemStack;

public interface PowerConsumer extends PowerListener, PlaceListener,BlockInterface, BreakListener{
	
	
	public default void addPower(float power, PowerGenerator source) {
		getGenerators().put(source,power);
		Main.log(this+" added source "+source);
	}

	public default void removePower(PowerGenerator source) {
		getGenerators().remove(source);
		Main.log(this+" removed source "+source);
		
	}
	
	public HashMap<PowerGenerator, Float> getGenerators();
	
	@Override
	default void placed(BlockFace SelectedFace) {
		refreshNearby();
		
	}
	
	public default void refreshNearby() {
		for(Block b : getWorld().get6Blocks((Block)this, false).values()) {
			if(b instanceof PowerPropagator pp) {
				pp.refreshGenerators(new LinkedList<>());
			}else if(b instanceof PowerGenerator pg) {
				pg.refresh();
			}
		}
	}
	
	@Override
	default ItemStack[] breaked(String username) {
		refreshNearby();
		return null;
	}

}
