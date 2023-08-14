package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.Surface;

public class Nothing extends Block {
	
	private static final BlockDescriptor descriptor = new BlockDescriptor() {
		@Override
		public boolean isFullBlock() {
			return false;
		}
		
		@Override
		public boolean isSolid() {
			return false;
		}
		
		@Override
		public boolean isTransparent() {
			return false;
		}
	};

	public Nothing(){
		super(0,0,0,null);
		name = "NOTHING";
	}

	@Override
	public Surface[] getSurfaces() {
		return null;
	}
	
	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor;
	}
	
	
}
