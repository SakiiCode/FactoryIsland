package ml.sakii.factoryisland.blocks;


import java.awt.Color;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;

public class DrillBlock extends SimpleMachineConsumer{
	
	public static Surface[] surfaces = Block.generateSurfaces(Main.drillSideColor);

	
	public DrillBlock(int x, int y, int z, GameEngine engine) {
		super("Drill", x, y, z, 
				Main.drillSideColor,
				Main.drillGradientBeginColor, new Color4(Color.RED), engine);
				
	}


	@Override
	public Surface[] getSurfaces() {
		return new Surface[] {new Surface(Main.drillSide, Main.drillGradientBeginColor.getColor()),
				new Surface(Main.drillSide, Main.drillGradientBeginColor.getColor()),
				new Surface(Main.drillSide, Main.drillGradientBeginColor.getColor()),
				new Surface(Main.drillSide, Main.drillGradientBeginColor.getColor()),
				new Surface(Main.drillSide, Main.drillGradientBeginColor.getColor()),
				new Surface(Main.drillSide, Main.drillGradientBeginColor.getColor())};
	}


	@Override
	void work() {
		BlockFace target = getTarget();
		Engine.world.destroyBlock(Engine.world.getBlockAt(x+target.direction[0], y+target.direction[1], z+target.direction[2]), true);
	}
	
	
}
