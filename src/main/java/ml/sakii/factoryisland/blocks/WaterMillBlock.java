package ml.sakii.factoryisland.blocks;


import java.awt.GradientPaint;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.SignalGeneratorComponent;
import ml.sakii.factoryisland.blocks.components.TickUpdateComponent;

public class WaterMillBlock extends SimpleMachine {

	SignalGeneratorComponent sgc;
	TickUpdateComponent tuc;
	
	public WaterMillBlock(int x, int y, int z, GameEngine engine){
		super("WaterMill", x, y, z,new Surface(AssetLibrary.wmSideColor),new Surface(AssetLibrary.wmGradientBeginColor),AssetLibrary.wmPoweredColor,AssetLibrary.waters[4].c, engine);
		sgc = new SignalGeneratorComponent(this, 10) {
			@Override
			public void refresh() {
				BlockFace target=getTarget();
				Block tBlock =Engine.world.getBlockAt(x+target.direction[0], y+target.direction[1], z+target.direction[2]); 
				BlockFace[] notTargetSides = new BlockFace[5];
				for(int i=0,idx=0;i<6;i++) {
					if(BlockFace.values[i] != target) {
						notTargetSides[idx]=BlockFace.values[i];
						idx++;
					}
				}
				if(tBlock instanceof WaterBlock && ((WaterBlock)tBlock).getHeight()<4){
					switchSignal(true,notTargetSides);
				}else {
					switchSignal(false,notTargetSides);
				}		
			}
		};
		addComponent(sgc);
		
	}

	@Override
	public Surface[] getSurfaces() {
		return new Surface[] {new Surface(AssetLibrary.wmSideColor, new GradientPaint(0, 0, AssetLibrary.wmGradientBeginColor.getColor(), 0, 0, Color4.TRANSPARENT)),
				new Surface(AssetLibrary.wmSideColor, new GradientPaint(0, 0, AssetLibrary.wmGradientBeginColor.getColor(), 0, 0, Color4.TRANSPARENT)),
				new Surface(AssetLibrary.wmSideColor, new GradientPaint(0, 0, AssetLibrary.wmGradientBeginColor.getColor(), 0, 0, Color4.TRANSPARENT)),
				new Surface(AssetLibrary.wmSideColor, new GradientPaint(0, 0, AssetLibrary.wmGradientBeginColor.getColor(), 0, 0, Color4.TRANSPARENT)),
				new Surface(AssetLibrary.wmSideColor, new GradientPaint(0, 0, AssetLibrary.wmGradientBeginColor.getColor(), 0, 0, Color4.TRANSPARENT)),
				new Surface(AssetLibrary.wmSideColor, new GradientPaint(0, 0, AssetLibrary.wmGradientBeginColor.getColor(), 0, 0, Color4.TRANSPARENT))};
	}


}
