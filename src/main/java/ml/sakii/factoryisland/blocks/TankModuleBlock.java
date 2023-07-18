package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class TankModuleBlock extends Block{
	
	private static Surface[] surfaces = Block.generateSurfaces(AssetLibrary.tankModule);
	
	public TankModuleBlock(int x, int y, int z, GameEngine engine){
		super("TankModule", x, y, z, engine);
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

}
