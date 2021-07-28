package ml.sakii.factoryisland.blocks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.items.ItemStack;

public interface PowerGenerator extends PowerListener, BreakListener, BlockInterface, TickListener{

	

	default void setPower(float power){
		setBlockMeta("generating",power+"",true);
		if(power==0) {
			for(PowerConsumer c : getConsumerCache()) {
				c.removePower(this);
			}
			return;
		}
		HashMap<BlockFace,Block> blocks = getWorld().get6Blocks((Block)this, false);
		HashSet<PowerConsumer> totalConsumers = new HashSet<>();
		// TODO ne kelljen ennyiszer ennyiféleképpen végigmenni
		for(BlockFace face:BlockFace.values) {
			
			if(blocks.get(face)!=null) {
				
				Block pl = blocks.get(face);
				if(pl instanceof PowerPropagator pp) {
					LinkedList<PowerListener> alreadyTested=new LinkedList<>();
					if(!pp.testPropagate(alreadyTested,totalConsumers)) { // van kábel de nincs fogyasztó rákötve
						blocks.remove(face);
					}
				}else if(!(pl instanceof PowerConsumer)){ // minden törlése ami nem fogyasztó vagy kábel
					blocks.remove(face);
				}
			}
				
				
			
		}
		Main.log("connected blocks: "+blocks.size()+", total consumers: "+totalConsumers.size());
		if(blocks.size()==0) return;
		
		float energyPerSide=power/totalConsumers.size();
		HashSet<PowerConsumer> results = new HashSet<>();
		for(Block b : blocks.values()) {
			if(b instanceof PowerPropagator pp) {
				
				pp.propagate(energyPerSide, this, new LinkedList<PowerListener>(), results);
			}else if(b instanceof PowerConsumer pc) {
				pc.addPower(energyPerSide, this);
			}
		}
		
		getConsumerCache().clear();
		getConsumerCache().addAll(results);
		
	}
	
	default void refresh() {
		setPower(0);
		setPower(Float.parseFloat(getBlockMeta().get("generating")));
	}
	
	@Override
	public default ItemStack[] breaked(String username) {
		setPower(0);
		return null;
	}
	
	abstract HashSet<PowerConsumer> getConsumerCache();
	
	abstract float getDesiredPower();
	
	@Override
	public default boolean tick(long tickCount) {
		setPower(getDesiredPower());
		return false;
	}
}
