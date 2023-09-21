package ml.sakii.factoryisland.blocks.components;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public class SignalGeneratorComponent extends SignalComponent {

	public SignalGeneratorComponent(Block block) {
		super(block);
	}

	public void setOutput(int intensity, BlockFace... sides) {
		List<BlockFace> activeSides = Arrays.asList(sides);
		for (Entry<BlockFace, Block> e : block.Engine.world.get6Blocks(block.pos.cpy(), false).entrySet()) {
			BlockFace face = e.getKey();
			Block b = e.getValue();

			if (activeSides.contains(face)) {
				for (Component c : b.Components) {
					if (c instanceof SignalPropagatorComponent spp) {
						spp.spreadPower(intensity, face.getOpposite());
					} else if (c instanceof SignalConsumerComponent scp) {
						scp.addSignal(intensity, face.getOpposite());
					}
				}

			}
		}
	}
	
	@Override
	public String toString() {
		return "SignalGenerator("+getCharge()+")";
	}

}
