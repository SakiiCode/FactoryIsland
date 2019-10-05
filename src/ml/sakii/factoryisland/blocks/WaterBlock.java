package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class WaterBlock extends Fluid {


	
	public WaterBlock(int x, int y, int z, GameEngine engine) {
		this(x,y,z,4,engine);
	}
	
	public WaterBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Water",x, y, z,height,Main.waters,engine);

	}
	
}

