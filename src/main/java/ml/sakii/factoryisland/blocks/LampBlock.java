package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class LampBlock extends Block{

	public static Surface[] surfaces = new Surface[] {Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp};
	
	public LampBlock(int x, int y, int z, GameEngine engine) {
		super("Lamp", x, y, z, 0.5f, 0.5f, 0.5f, engine);
		lightLevel=MAXLIGHT;
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	

}
