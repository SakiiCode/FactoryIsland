package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class StoneBlock extends Block{
	
	public static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.stone);
	
	public StoneBlock(int x, int y, int z,GameEngine engine){
		super("Stone",x, y, z, engine);

	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

}
