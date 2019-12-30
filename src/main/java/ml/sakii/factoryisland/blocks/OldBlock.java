package ml.sakii.factoryisland.blocks;

import java.awt.Color;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class OldBlock extends Block {

	public OldBlock(int x, int y, int z,GameEngine engine){
		super("Old",x, y, z, new Surface(Color.cyan), new Surface(Color.ORANGE), new Surface(Color.darkGray), new Surface(Color.gray), new Surface(Color.blue), new Surface(Color.magenta),engine);
	}
	
	
}
