package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Polygon3D;

public class LampBlock extends Block implements PlaceListener, LightListener, LoadListener{

	//public int lightLevel=10;
	//lightLevel=15;
	public LampBlock(int x, int y, int z, GameEngine engine) {
		super("Lamp", x, y, z, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, Main.lamp, 0.5f, 0.5f, 0.5f, engine);
		lightLevel=MAXLIGHT;
		for(Polygon3D poly : Polygons) {
			poly.addSource(this, lightLevel);
		}
		
	}

	@Override
	public void placed(BlockFace SelectedFace) {
		Engine.world.addLight(this.x, this.y, this.z, this, lightLevel, null);
	}

	@Override
	public void onLoad(){
		Engine.world.addLight(this.x, this.y, this.z, this, lightLevel, null);
	}

	/*@Override
	public boolean breaked(String username)
	{
		//Engine.world.removeLight(this.x, this.y, this.z, this, lightLevel);
		return false;
	}*/
	

}
