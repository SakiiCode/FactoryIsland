package ml.sakii.factoryisland.blocks.components;

import java.util.Map.Entry;

import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class SignalPropagatorComponent extends SignalComponent{
	
	public SignalPropagatorComponent(Block block) {
		super(block);
	}
	
	public void spreadSignal(int targetPower, BlockFace source){
		if(targetPower > 0) {
			if(signals.containsKey(source) && signals.get(source)>targetPower) {
				return;
			}
			signals.put(source, targetPower);
		}else {
			if(!signals.containsKey(source)) {
				return;
			}
			signals.remove(source);
		}
		onSignalUpdate();
		
		
		
		for(Entry<BlockFace, Block> entry : block.Engine.world.get6Blocks(new Point3D().set(block.pos), false).entrySet()){
			Block b = entry.getValue();
			BlockFace face = entry.getKey();
			if(face == source) {
				continue;
			}
			for(Component c : b.getComponents()) {
				if(c instanceof SignalPropagatorComponent sp) {
					sp.spreadSignal(Math.max(targetPower-1,0), face.getOpposite());
				}else if(c instanceof SignalConsumerComponent scc) {
					scc.addSignal(targetPower, face.getOpposite());
				}
			}
		}
	}
	
	public void onSignalUpdate() {
		
	}
	
	@Override
	public String toString() {
		return "SignalPropagator("+getCharge()+")";
	}
	

}
