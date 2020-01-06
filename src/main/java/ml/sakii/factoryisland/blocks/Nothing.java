package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.Surface;

public class Nothing extends Block {

	public Nothing(){
		super(0,0,0,null);
		name = "NOTHING";
		fullblock=false;
		solid = false;
		transparent=false;
	}

	@Override
	public Surface[] getSurfaces() {
		return null;
	}
}
