package ml.sakii.factoryisland.blocks.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.items.ItemStack;

public class PowerConsumerComponent extends PowerComponent {

	HashMap<PowerGeneratorComponent, Float> generators = new HashMap<>();
	BreakComponent bc;
	PlaceComponent pc;
	
	
	public PowerConsumerComponent(Block block) {
		super(block);
		
		bc = new BreakComponent(block) {
			@Override
			public List<ItemStack> onBreak() {
				refreshNearby();
				return new ArrayList<>();
			}
		};
		SubComponents.add(bc);
		
		pc = new PlaceComponent(block) {
			@Override
			public void placed(BlockFace selectedFace) {
				refreshNearby();
			}
		};
		SubComponents.add(pc);
	}
	
	public void addPower(float power, PowerGeneratorComponent source) {
		generators.put(source,power);
		Main.log(this+" added source "+source);
		block.setMetadata("power",getPower()+"", true);
		
	}

	public void removePower(PowerGeneratorComponent source) {
		generators.remove(source);
		Main.log(this+" removed source "+source);
		
		block.setMetadata("power",getPower()+"", true);
		
	}
	
	public float getPower() {
		float sum = 0;
		for(Float f : generators.values()) {
				sum += f;
		}
		return sum;
	}
	
	private void refreshNearby() {
		for(Block b : block.Engine.world.get6Blocks(block, false).values()) {
			for(PowerPropagatorComponent ppc : b.getComponents(PowerPropagatorComponent.class)) {
				ppc.refreshGenerators(new LinkedList<>());
			}
			for(PowerGeneratorComponent pgc : b.getComponents(PowerGeneratorComponent.class)) {
				pgc.refresh();
			}
		}
	}

}
