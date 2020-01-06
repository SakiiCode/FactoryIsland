package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class StoneBlock extends Block{
	
	public static Surface[] surfaces = new Surface[] {Main.stone, Main.stone, Main.stone, Main.stone,Main.stone,Main.stone};
	
	public StoneBlock(int x, int y, int z,GameEngine engine){
		super("Stone",x, y, z, engine);

	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

}
