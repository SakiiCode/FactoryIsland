package ml.sakii.factoryisland.blocks.components;

import java.util.ArrayList;

import ml.sakii.factoryisland.blocks.Block;

public class Component {
	
	public ArrayList<Component> SubComponents = new ArrayList<>();
	protected final Block block;
	
	public Component(Block block) {
		this.block = block;
	}

}
