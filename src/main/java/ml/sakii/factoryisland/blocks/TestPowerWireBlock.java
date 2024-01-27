package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.PowerPropagatorComponent;

public class TestPowerWireBlock extends Block{

	private static Surface[] surfaces = Block.generateSurfaces(new Color4(Color.orange));
	
	PowerPropagatorComponent ppc;
	
	public TestPowerWireBlock(int x, int y, int z, GameEngine engine) {
		super("TestPowerWire",x, y, z, engine);
		ppc = new PowerPropagatorComponent(this);
		addComponent(ppc);
	}


	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

}
