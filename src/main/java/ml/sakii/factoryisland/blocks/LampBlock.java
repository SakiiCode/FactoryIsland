package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Globals;
import ml.sakii.factoryisland.Surface;

public class LampBlock extends Block{

	public static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.lamp);
	
	public LampBlock(int x, int y, int z, GameEngine engine) {
		super("Lamp", x, y, z, 0.5f, 0.5f, 0.5f, engine);
		lightLevel=Globals.MAXLIGHT;
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	

}
