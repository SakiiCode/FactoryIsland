package ml.sakii.factoryisland.blocks;


import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class SandBlock extends Block{
	

	private static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.sand);
	
	public SandBlock(int x, int y, int z, GameEngine engine){
		super("Sand",x, y, z,engine);
		
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	

	
	
	
	
}
