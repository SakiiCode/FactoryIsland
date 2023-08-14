package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Globals;
import ml.sakii.factoryisland.Surface;

public class LampBlock extends Block{

	private static final Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.lamp);
	
	private static final BlockDescriptor descriptor = new BlockDescriptor() {

		@Override
		public boolean isFullBlock() {
			return false;
		}
		
		@Override
		public int getLightLevel() {
			return Globals.MAXLIGHT;
		}
	};
	
	public LampBlock(int x, int y, int z, GameEngine engine) {
		super("Lamp", x, y, z, 0.5f, 0.5f, 0.5f, engine);
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor;
	}
	
	

}
