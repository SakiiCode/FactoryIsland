package ml.sakii.factoryisland.blocks.components;

import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.items.ItemStack;

public class SignalPropagatorComponent extends SignalComponent{
	
	BreakComponent bc;
	TickUpdateComponent tuc;
	
	public SignalPropagatorComponent(Block block) {
		super(block);
		
		bc = new BreakComponent(block) {
			@Override
			public List<ItemStack> onBreak() {
				if(getCharge()>1) {
					spreadSignal(0,false);
				}
				return null;
			}
		};
		SubComponents.add(bc);
		
		tuc = new TickUpdateComponent(block) {
			
			@Override
			public boolean onTick(long tick) {
				int charge = getCharge();
				if(charge>1) {
					spreadSignal(getCharge()-1, true);
				}
				return false;
			}
		};
		
		SubComponents.add(tuc);
	}
	
	@Override
	public void addSignal(int power, BlockFace relativeFrom) {
		if(signals.get(relativeFrom) == null  || signals.get(relativeFrom) < power){
			signals.put(relativeFrom, power);
			if(power>1){
				spreadSignal(power-1,true);
			}
			block.setMetadata("signalLevel", getCharge()+"", true);
		}
	}
	
	
	private void spreadSignal(int targetPower, boolean add){
		//TODO do not clear every signal on block delete
		for(Entry<BlockFace, Block> entry : block.Engine.world.get6Blocks(new Point3D().set(block.pos), false).entrySet()){
			Block b = entry.getValue();
			BlockFace face = entry.getKey();
			for(Component c : b.getComponents()) {
				if(c instanceof SignalComponent pw) {
					if(add) {
						pw.addSignal(targetPower, face.getOpposite());
					}else {
						pw.removeSignal(face.getOpposite());
					}
				}
			}
		}
	}
	
	@Override
	public void removeSignal(BlockFace relativeFrom) {
		if(signals.containsKey(relativeFrom)) {
			signals.remove(relativeFrom);
			spreadSignal(0,false);
			block.setMetadata("signalLevel", getCharge()+"", true);
		}
		
	}
		
	@Override
	public String toString() {
		return "SignalPropagator("+getCharge()+")";
	}
	

}
