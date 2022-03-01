package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class OilBlock extends Fluid{

	public static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.oils[3]); // ez a viewmodelhez és az inv ikonhoz kell csak


	public OilBlock(int x, int y, int z, GameEngine engine) {
		this(x, y, z, 3, engine);
	}

	public OilBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Oil",x, y, z,height,AssetLibrary.oils,engine);
		refreshRate = 20;

	}
	
	@Override
	public Surface[] getSurfaces() { //játékbeli kocka generálásához
		int height = getHeight();
		return Block.generateSurfacesCopy(AssetLibrary.oils[height]);
	}


}
