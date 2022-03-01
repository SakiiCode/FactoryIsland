package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class WaterBlock extends Fluid {

	public static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.waters[3]); // ez a viewmodelhez és az inv ikonhoz kell csak

	
	public WaterBlock(int x, int y, int z, GameEngine engine) {
		this(x,y,z,4,engine);
	}
	
	public WaterBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Water",x, y, z,height,AssetLibrary.waters,engine);

	}

	@Override
	public Surface[] getSurfaces() {
		int height = getHeight();
		return Block.generateSurfacesCopy(AssetLibrary.waters[height]);

	}
	
}

