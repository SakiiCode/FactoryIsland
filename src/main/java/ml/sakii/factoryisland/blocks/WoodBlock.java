package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class WoodBlock extends SignalPropagator{

	public WoodBlock(int x, int y, int z, GameEngine engine) {
		super("Wood", x, y, z,engine);

	}


	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(AssetLibrary.wood.Texture,Color4.TRANSPARENT));
	}
	

	
}
