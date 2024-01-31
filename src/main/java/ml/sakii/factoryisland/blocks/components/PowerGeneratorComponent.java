package ml.sakii.factoryisland.blocks.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.items.ItemStack;

public abstract class PowerGeneratorComponent extends PowerComponent {

	HashSet<PowerConsumerComponent> consumers = new HashSet<>();
	
	BreakComponent bc;
	TickUpdateComponent tuc;
	
	public PowerGeneratorComponent(Block block) {
		super(block);
		bc = new BreakComponent(block) {
			
			@Override
			public List<ItemStack> onBreak() {
				setPower(0);
				return new ArrayList<>();
			}
		};
		SubComponents.add(bc);
		
		tuc = new TickUpdateComponent(block) {
			// automatically runs on world load
			@Override
			public boolean onTick(long tick) {
				Main.log("Setting power to "+getDesiredPower());
				setPower(getDesiredPower());
				return false;
			}
		};
		SubComponents.add(tuc);
		
	}
	
	public void setPower(float power){
		block.setMetadata("generating",power+"",true);
		HashMap<BlockFace,Block> blocks = block.Engine.world.get6Blocks(block, false);
		HashSet<PowerConsumerComponent> totalConsumers = new HashSet<>();
		for(BlockFace face:BlockFace.values) {
			
			if(blocks.get(face)!=null) {
				
				Block pl = blocks.get(face);
				for(PowerPropagatorComponent ppc : pl.getComponents(PowerPropagatorComponent.class)) {
					LinkedList<Block> alreadyTested=new LinkedList<>();
					if(ppc.findConsumers(alreadyTested,totalConsumers) == 0) { // van kábel de nincs fogyasztó rákötve
						blocks.remove(face);
					}
				}
				for(@SuppressWarnings("unused") PowerConsumerComponent pcc : pl.getComponents(PowerConsumerComponent.class)) { // minden törlése ami nem fogyasztó vagy kábel
					blocks.remove(face);
				}
			}
				
				
			
		}
		Main.log("nearby blocks: "+blocks.size()+", total consumers: "+totalConsumers.size());
		if(blocks.size()==0) return;
		
		float energyPerSide=power/totalConsumers.size();
		HashSet<PowerConsumerComponent> results = new HashSet<>();
		for(Block b : blocks.values()) {
			for(PowerPropagatorComponent ppc : b.getComponents(PowerPropagatorComponent.class)) {
				ppc.propagate(energyPerSide, this, new LinkedList<>(), results);
			}
			for(PowerConsumerComponent pcc : b.getComponents(PowerConsumerComponent.class)) {
				pcc.addPower(energyPerSide, this);
			}
		}
		
		consumers.clear();
		consumers.addAll(results);
		
	}
	
	void refresh() {
		float generating =Float.parseFloat(block.getMetadata("generating")); 
		setPower(0);
		setPower(generating);
	}
	
	public abstract float getDesiredPower();

}
