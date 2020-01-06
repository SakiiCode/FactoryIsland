package ml.sakii.factoryisland.blocks;


import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class SandBlock extends Block{
	

	public static Surface[] surfaces = new Surface[] {Main.sand, Main.sand, Main.sand, Main.sand, Main.sand, Main.sand};
	
	public SandBlock(int x, int y, int z, GameEngine engine){
		super("Sand",x, y, z,engine);
		
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	

	
	
	
	
}
