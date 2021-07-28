package ml.sakii.factoryisland.blocks;

import java.util.HashSet;
import java.util.LinkedList;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.items.ItemStack;

@SuppressWarnings("unlikely-arg-type")
public interface PowerPropagator extends PowerListener, BlockInterface, PlaceListener, BreakListener{

	
	
	public default boolean testPropagate(LinkedList<PowerListener> alreadyTested, HashSet<PowerConsumer> results) { //legalabb 1 fogyaszto ra van kotve
		alreadyTested.add(this);
		for(Block b : getWorld().get6Blocks((Block)this, false).values()) {
			if(b instanceof PowerConsumer pc) {
				Main.log("Found consumer at " +pc);
				results.add(pc);
			}else if(b instanceof PowerPropagator pp) {
				if(!alreadyTested.contains(b)) {
					pp.testPropagate(alreadyTested,results);
				}
			}
		}
		return results.size()>0;
	}
	
	
	public default void propagate(float power, PowerGenerator source, LinkedList<PowerListener> alreadyTested, HashSet<PowerConsumer> results) {
		alreadyTested.add(this);
		if(power==0) {
			getGeneratorCache().remove(source);
		}else {
			getGeneratorCache().add(source);
		}
		
		for(Block b : getWorld().get6Blocks((Block)this, false).values()) {
			if(alreadyTested.contains(b)) continue;
			
			if(b instanceof PowerConsumer pc) {
				alreadyTested.add(pc);
				if(power==0) {
					pc.removePower(source);
				}else {
					pc.addPower(power, source);
				}
				results.add(pc);
			}else if(b instanceof PowerPropagator pp) {
				pp.propagate(power,source,alreadyTested,results);
			}
		}
	}
	
	default void refreshGenerators(LinkedList<PowerListener> alreadyTested) {
		alreadyTested.add(this);
		for(Block b : getWorld().get6Blocks((Block)this, false).values()) {
			if(alreadyTested.contains(b)) continue;
			if(b instanceof PowerGenerator pg) {
				alreadyTested.add(pg);
				pg.refresh();
			}else if(b instanceof PowerPropagator pp) {
				pp.refreshGenerators(alreadyTested);
			}
		}
	}
	
	
	@Override
	default void placed(BlockFace face) {
		refreshGenerators(new LinkedList<>());
	}
	
	@Override
	default ItemStack[] breaked(String username) {
		for(PowerGenerator g : new HashSet<>(getGeneratorCache())) {
			g.refresh();
		}
		return null;
	}
	
	abstract HashSet<PowerGenerator> getGeneratorCache();
	



}
