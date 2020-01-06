package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class WaterBlock extends Fluid {

	public static Surface[] surfaces = new Surface[] {Main.waters[3], Main.waters[3], Main.waters[3], Main.waters[3], Main.waters[3], Main.waters[3]}; // ez a viewmodelhez és az inv ikonhoz kell csak

	
	public WaterBlock(int x, int y, int z, GameEngine engine) {
		this(x,y,z,4,engine);
	}
	
	public WaterBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Water",x, y, z,height,Main.waters,engine);

	}

	@Override
	public Surface[] getSurfaces() {
		int height = getHeight();
		return new Surface[] {Main.waters[height], Main.waters[height], Main.waters[height], Main.waters[height], Main.waters[height], Main.waters[height]};
	}
	
}

