package ml.sakii.factoryisland.blocks;


import java.awt.GradientPaint;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class WaterMillBlock extends SimpleMachineGenerator {
	
	public static Surface[] surfaces = Block.generateSurfaces(Main.wmSideColor);

	public WaterMillBlock(int x, int y, int z, GameEngine engine){
		super("WaterMill", x, y, z,Main.wmSideColor,Main.wmGradientBeginColor,Main.wmPoweredColor,Main.waters[4].c, engine);

	}

	@Override
	public Surface[] getSurfaces() {
		return new Surface[] {new Surface(Main.wmSideColor, new GradientPaint(0, 0, Main.wmGradientBeginColor.getColor(), 0, 0, Main.TRANSPARENT)),
				new Surface(Main.wmSideColor, new GradientPaint(0, 0, Main.wmGradientBeginColor.getColor(), 0, 0, Main.TRANSPARENT)),
				new Surface(Main.wmSideColor, new GradientPaint(0, 0, Main.wmGradientBeginColor.getColor(), 0, 0, Main.TRANSPARENT)),
				new Surface(Main.wmSideColor, new GradientPaint(0, 0, Main.wmGradientBeginColor.getColor(), 0, 0, Main.TRANSPARENT)),
				new Surface(Main.wmSideColor, new GradientPaint(0, 0, Main.wmGradientBeginColor.getColor(), 0, 0, Main.TRANSPARENT)),
				new Surface(Main.wmSideColor, new GradientPaint(0, 0, Main.wmGradientBeginColor.getColor(), 0, 0, Main.TRANSPARENT))};
	}
	
	@Override
	void refresh(){
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
			switchPower(true,notTargetSides);
		}else {
			switchPower(false,notTargetSides);
		}
	}


}
