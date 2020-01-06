package ml.sakii.factoryisland.blocks;

import java.awt.Color;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class OldBlock extends Block{

	
	public static Surface[] surfaces = new Surface[] {new Surface(Color.cyan), new Surface(Color.ORANGE), new Surface(Color.darkGray), new Surface(Color.gray), new Surface(Color.blue), new Surface(Color.magenta)};
	
	public OldBlock(int x, int y, int z,GameEngine engine){
		super("Old",x, y, z,engine);
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}

	
	
}
