package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class LampBlock extends Block{

	//public int lightLevel=10;
	//lightLevel=15;
	public LampBlock(int x, int y, int z, GameEngine engine) {
		super("Lamp", x, y, z, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, 0.5f, 0.5f, 0.5f, engine);
	}

	

}
