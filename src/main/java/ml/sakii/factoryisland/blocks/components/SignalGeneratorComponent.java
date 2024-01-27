package ml.sakii.factoryisland.blocks.components;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.items.ItemStack;

public abstract class SignalGeneratorComponent extends Component {
	
	TickUpdateComponent tuc;
	BreakComponent bc;
	
	private int strength;

	public SignalGeneratorComponent(Block block, int strength) {
		super(block);
		
		this.strength = strength;
		
		tuc = new TickUpdateComponent(block) {
			
			@Override
			public boolean onTick(long tick) {
				refresh();
				return false;
			}
		};
		
		SubComponents.add(tuc);
		
		bc = new BreakComponent(block) {
			
			@Override
			public List<ItemStack> onBreak() {
				switchSignal(false, BlockFace.values);
				return null;
			}
		};
	}
	
	@Override
	public String toString() {
		return "SignalGenerator("+block.getAllMetadata().toString()+")";
	}
	
	public void switchSignal(boolean on, BlockFace[] sides) {
		if(on) {
			block.setMetadata("active", "1", true);
		}else {
			block.setMetadata("active", "0", true);	
		}
		List<BlockFace> activeSides = Arrays.asList(sides);
		for(Entry<BlockFace, Block> e : block.getWorld().get6Blocks(block, false).entrySet()){
			BlockFace face = e.getKey();
			Block b = e.getValue();
			
			if(activeSides.contains(face)) {
				for(Component c : b.getComponents()) {
					if(c instanceof SignalComponent sc) {
						if(on) {
							sc.addSignal(strength, face.getOpposite());
						}else {
							sc.removeSignal(face.getOpposite());
						}
					}
				}
				
			}
		}
	}
	
	public abstract void refresh();

}
