package ml.sakii.factoryisland.blocks.components;

import java.util.List;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.items.ItemStack;

public abstract class BreakComponent extends Component {

	public BreakComponent(Block block) {
		super(block);
	}

	public abstract List<ItemStack> onBreak();

}
