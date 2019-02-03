package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class TankModuleBlock extends Block{
	
	public TankModuleBlock(int x, int y, int z, GameEngine engine){
		super("TankModule", x, y, z,  
				new Surface(Main.tankModule),
				new Surface(Main.tankModule),
				new Surface(Main.tankModule),
				new Surface(Main.tankModule),
				new Surface(Main.tankModule),
				new Surface(Main.tankModule),
				engine);
	}

}
