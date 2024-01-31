package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.SignalPropagatorComponent;
import ml.sakii.factoryisland.blocks.components.WorldLoadComponent;

public class WoodBlock extends Block implements MetadataListener{
	
	SignalPropagatorComponent spc;
	WorldLoadComponent wlc;

	public WoodBlock(int x, int y, int z, GameEngine engine) {
		super("Wood", x, y, z,engine);
		
		spc = new SignalPropagatorComponent(this);
		addComponent(spc);
	}


	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(AssetLibrary.wood.Texture,Color4.TRANSPARENT));
	}
	
	private void recalcPaints(int charge) {
		for(Object3D obj : Objects){
			if(obj instanceof Polygon3D p) {
				if(charge == 0){
					p.s.paint = false;
				}else{
					p.s.p = new Color(AssetLibrary.fire.c.getRed()/255f,
							AssetLibrary.fire.c.getGreen()/255f,
							AssetLibrary.fire.c.getBlue()/255f,
							Math.max(0,Math.min(10, charge))/10f);
					p.s.paint = true;
				}
			}
		}
	}
	
	@Override
	public boolean onMetadataUpdate(String key, String value) {
		if(key.equals("signalLevel")) {
			storeMetadata(key, value);
			recalcPaints(Integer.parseInt(value));
			return true;
		}
		return false;
	}
	
}
