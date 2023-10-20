package ml.sakii.factoryisland.blocks.components;

import java.util.Map.Entry;

import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class PowerPropagatorComponent extends PowerComponent{

	public PowerPropagatorComponent(Block block) {
		super(block);
	}
	
	public void spreadPower(int targetPower, BlockFace source){
		if(targetPower > 0) {
			if(powers.containsKey(source) && powers.get(source)>targetPower) {
				return;
			}
			powers.put(source, targetPower);
		}else {
			if(!powers.containsKey(source)) {
				return;
			}
			powers.remove(source);
		}
		onSignalUpdate();
		
		
		
		for(Entry<BlockFace, Block> entry : block.Engine.world.get6Blocks(new Point3D().set(block.pos), false).entrySet()){
			Block b = entry.getValue();
			BlockFace face = entry.getKey();
			if(face == source) {
				continue;
			}
			for(Component c : b.getComponents()) {
				if(c instanceof PowerPropagatorComponent sp) {
					sp.spreadPower(Math.max(targetPower,0), face.getOpposite());
				}else if(c instanceof PowerConsumerComponent scc) {
					scc.addPower(targetPower, face.getOpposite());
				}
			}
		}
	}
	
	public void onSignalUpdate() {
		
	}

}
