package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class StoneBlock extends Block{
	
	public StoneBlock(int x, int y, int z,GameEngine engine){
		super("Stone",x, y, z,Main.stone, Main.stone, Main.stone, Main.stone,Main.stone,Main.stone, engine);

	}

}
