package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class GrassBlock extends Block{
	
	public static Surface[] surfaces = new Surface[] {Main.grass,Main.dirt,Main.dirt,Main.dirt,Main.dirt,Main.dirt};
	
	public GrassBlock(int x, int y, int z,GameEngine engine){
		super("Grass",x, y, z,engine);
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}	
	


	
	
	
}
