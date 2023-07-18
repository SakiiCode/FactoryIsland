package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import java.util.HashMap;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class TestPowerConsumerBlock extends SimpleMachine implements PowerConsumer{

	public TestPowerConsumerBlock(int x, int y, int z, GameEngine engine) {
		super("TestPowerConsumer", x, y, z, AssetLibrary.stone.c, new Color4(0,0,0,255), AssetLibrary.stone.c, new Color4(Color.GRAY), engine);
		
	}

	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(AssetLibrary.stone.Texture,Color.black));
	}

	private HashMap<PowerGenerator, Float> generators = new HashMap<>();

	
	@Override
	public HashMap<PowerGenerator, Float> getGenerators() {
		return generators;
	}
	


}
