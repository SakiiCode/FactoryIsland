package ml.sakii.factoryisland.blocks;


import java.awt.Color;
import java.util.HashMap;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class DrillBlock extends SimpleMachine implements SignalConsumer{
	
	public static Surface[] surfaces = Block.generateSurfaces(AssetLibrary.drillSideColor);
	private HashMap<BlockFace, Integer> signals = new HashMap<>();
	
	
	public DrillBlock(int x, int y, int z, GameEngine engine) {
		super("Drill", x, y, z, 
				AssetLibrary.drillSideColor,
				AssetLibrary.drillGradientBeginColor,AssetLibrary.drillSideColor, new Color4(Color.RED), engine);
				
	}


	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(AssetLibrary.drillSide, AssetLibrary.drillGradientBeginColor.getColor()));
	}


	@Override
	public void work() {
		BlockFace target = getTarget();
		Block targetBlock = Engine.world.getBlockAt(x+target.direction[0], y+target.direction[1], z+target.direction[2]);
		if(targetBlock != Block.NOTHING) {
			Engine.world.destroyBlock(targetBlock, true);
		}
	}


	@Override
	public HashMap<BlockFace, Integer> getSignals() {
		return signals;
	}
	
	
}
