package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.SignalConsumerComponent;

public class DrillBlock extends SimpleMachine {
	public DrillBlock(int x, int y, int z, GameEngine engine) {
		super("Drill", x, y, z, AssetLibrary.drillSideColor, AssetLibrary.drillGradientBeginColor,
				AssetLibrary.drillSideColor, new Color4(Color.RED), engine);

		addComponent(new SignalConsumerComponent(this) {
			@Override
			public void addSignal(int intensity, BlockFace source) {
				super.addSignal(intensity, source);
			}

			@Override
			public void turnOn() {
				work();
			}
		});

	}

	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(
				new Surface(AssetLibrary.drillSide, AssetLibrary.drillGradientBeginColor.getColor()));
	}

	public void work() {
		BlockFace target = getTarget();
		Block targetBlock = Engine.world.getBlockAt(x + target.direction[0], y + target.direction[1],
				z + target.direction[2]);
		if (targetBlock != Block.NOTHING) {
			Engine.world.destroyBlock(targetBlock, true);
		}
	}

}
