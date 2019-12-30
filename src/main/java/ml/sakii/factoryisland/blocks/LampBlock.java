package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class LampBlock extends Block{

	//public int lightLevel=10;
	//lightLevel=15;
	public LampBlock(int x, int y, int z, GameEngine engine) {
		super("Lamp", x, y, z, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, 0.5f, 0.5f, 0.5f, engine);
		setLight(MAXLIGHT);
		
	}

	/*@Override
	public void placed(BlockFace SelectedFace) {
	}*/

	/*@Override
	public void onLoad(){
		
	}*/

	/*@Override
	public boolean breaked(String username)
	{
		//Engine.world.removeLight(this.x, this.y, this.z, this, lightLevel);
		return false;
	}*/
	

}
