package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class GrassBlock extends Block{
	
	private static Surface[] surfaces = new Surface[] {
			AssetLibrary.grass,
			AssetLibrary.dirt,
			AssetLibrary.dirt,
			AssetLibrary.dirt,
			AssetLibrary.dirt,
			AssetLibrary.dirt};
	
	public GrassBlock(int x, int y, int z,GameEngine engine){
		super("Grass",x, y, z,engine);
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}	
	


	
	
	
}
