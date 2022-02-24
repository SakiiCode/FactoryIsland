package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class WoodBlock extends SignalPropagator{

	public static Surface[] surfaces = Block.generateSurfacesNoCopy(Main.wood);
	
	public WoodBlock(int x, int y, int z, GameEngine engine) {
		super("Wood", x, y, z,engine);

	}


	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(Main.wood.Texture,Color4.TRANSPARENT));
	}
	

	
}
