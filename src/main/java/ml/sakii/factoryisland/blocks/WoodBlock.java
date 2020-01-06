package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class WoodBlock extends PowerWire{

	public static Surface[] surfaces = new Surface[] {Main.wood,Main.wood,Main.wood,Main.wood,Main.wood,Main.wood};
	
	public WoodBlock(int x, int y, int z, GameEngine engine) {
		super("Wood", x, y, z,engine);

	}


	@Override
	public Surface[] getSurfaces() {
		return new Surface[] {new Surface(Main.wood.c, Main.TRANSPARENT),
				new Surface(Main.wood.c, Main.TRANSPARENT),
				new Surface(Main.wood.c, Main.TRANSPARENT),
				new Surface(Main.wood.c, Main.TRANSPARENT),
				new Surface(Main.wood.c, Main.TRANSPARENT),
				new Surface(Main.wood.c, Main.TRANSPARENT)};
	}
	

	
}
