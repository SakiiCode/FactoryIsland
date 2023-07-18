package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import java.util.HashSet;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class TestPowerWireBlock extends Block implements PowerPropagator{

	private static Surface[] surfaces = Block.generateSurfaces(new Color4(Color.orange));
	private HashSet<PowerGenerator> generatorCache = new HashSet<>();
	
	public TestPowerWireBlock(int x, int y, int z, GameEngine engine) {
		super("TestPowerWire",x, y, z, engine);
	}


	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}


	@Override
	public HashSet<PowerGenerator> getGeneratorCache() {
		return generatorCache;
	}

}
