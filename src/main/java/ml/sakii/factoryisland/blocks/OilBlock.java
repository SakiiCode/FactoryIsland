package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;

public class OilBlock extends Fluid{

	private static final BlockDescriptor descriptor = new BlockDescriptor() {
		@Override
		public boolean isSolid() {
			return false;
		}
		
		@Override
		public boolean isTransparent() {
			return true;
		}
		@Override
		public int getRefreshRate() {
			return 20;
		}
	};
	
	public OilBlock(int x, int y, int z, GameEngine engine) {
		this(x, y, z, 3, engine);
	}

	public OilBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Oil",x, y, z,height,engine);
	}
	
	@Override
	public Surface[] getSurfaces() { //játékbeli kocka generálásához
		int height = getHeight();
		return Block.generateSurfacesCopy(AssetLibrary.oils[height]);
	}
	
	@Override
	protected Surface[] getTextures() {
		return AssetLibrary.oils;
	}
	
	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor;
	}
	


}
