package ml.sakii.factoryisland.blocks.components;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.items.ItemStack;

public class PowerPropagatorComponent extends PowerComponent{

	public HashSet<PowerGeneratorComponent> generators = new HashSet<>();
	
	BreakComponent bc;
	PlaceComponent pc;
	
	public PowerPropagatorComponent(Block block) {
		super(block);
		bc = new BreakComponent(block) {
			
			@Override
			public List<ItemStack> onBreak() {
				refreshGenerators(new LinkedList<>());
				return null;
			}
		};
		SubComponents.add(bc);
		
		pc = new PlaceComponent(block) {
			
			@Override
			public void placed(BlockFace selectedFace) {
				refreshGenerators(new LinkedList<>());
			}
		};
		SubComponents.add(pc);
	}
	
	public int findConsumers(LinkedList<Block> alreadyTested, HashSet<PowerConsumerComponent> results) {
		alreadyTested.add(block);
		for(Block b : block.getWorld().get6Blocks(block, false).values()) {
			if(alreadyTested.contains(b)) {
				continue;
			}
			for(PowerConsumerComponent pcc : b.getComponents(PowerConsumerComponent.class)) {
				Main.log("Found consumer at " +b);
				results.add(pcc);
			}
			for(PowerPropagatorComponent ppc : b.getComponents(PowerPropagatorComponent.class)) {				
				ppc.findConsumers(alreadyTested,results);
			}
		}
		return results.size();
	}
	
	
	public void propagate(float power, PowerGeneratorComponent source, LinkedList<Block> alreadyTested, HashSet<PowerConsumerComponent> results) {
		alreadyTested.add(block);
		if(power==0) {
			generators.remove(source);
			refreshActive();
		}else {
			generators.add(source);
			refreshActive();
		}
		
		for(Block b : block.getWorld().get6Blocks(block, false).values()) {
			if(alreadyTested.contains(b)) continue;
			alreadyTested.add(b);
			
			for(PowerConsumerComponent pcc : b.getComponents(PowerConsumerComponent.class)) {
				if(power==0) {
					pcc.removePower(source);
				}else {
					pcc.addPower(power, source);
				}
				results.add(pcc);
			}
			
			for(PowerPropagatorComponent ppc : b.getComponents(PowerPropagatorComponent.class)) {
				ppc.propagate(power,source,alreadyTested,results);
				
			}
		}
	}
	
	public void refreshGenerators(LinkedList<Block> alreadyTested) {
		for(PowerGeneratorComponent generator : generators) {
			generators.remove(generator);
		}
		refreshActive();
		for(Block b : block.getWorld().get6Blocks(block, false).values()) {
			if(alreadyTested.contains(b)) continue;
			alreadyTested.add(b);
			for(PowerGeneratorComponent pgc : b.getComponents(PowerGeneratorComponent.class)) {
				pgc.refresh();
			}
			for(PowerPropagatorComponent ppc : b.getComponents(PowerPropagatorComponent.class)) {
				ppc.refreshGenerators(alreadyTested);
			}
		}
	}
	
	private void refreshActive() {
		block.setMetadata("active", generators.size()>0?"1":"0", true);
	}
	


}
