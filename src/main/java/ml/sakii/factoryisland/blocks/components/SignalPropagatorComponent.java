package ml.sakii.factoryisland.blocks.components;

import java.util.Map.Entry;

import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class SignalPropagatorComponent extends SignalComponent{
	
	public SignalPropagatorComponent(Block block) {
		super(block);
	}
	
	public void spreadPower(int targetPower){ 
		for(Entry<BlockFace, Block> entry : block.Engine.world.get6Blocks(new Point3D().set(block.pos), false).entrySet()){
			Block b = entry.getValue();
			BlockFace face = entry.getKey();
			
			for(Component c : b.Components) {
				if(c instanceof SignalPropagatorComponent sp) {
					sp.spreadPower(targetPower-1);
				}else if(c instanceof SignalConsumerComponent scc) {
					scc.addSignal(targetPower, face.getOpposite());
				}
			}
		}
	}
	
	public void onSignalUpdate() {
		
	}

}
