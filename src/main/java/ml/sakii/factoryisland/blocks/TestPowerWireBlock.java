package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.PowerPropagatorComponent;

public class TestPowerWireBlock extends Block implements MetadataListener {

	PowerPropagatorComponent ppc;
	
	private static Color4 enabled = new Color4(220,150,40);
	private static Color4 disabled = new Color4(150,90,15);
	
	public TestPowerWireBlock(int x, int y, int z, GameEngine engine) {
		super("TestPowerWire",x, y, z, engine);
		ppc = new PowerPropagatorComponent(this);
		addComponent(ppc);
	}


	@Override
	public Surface[] getSurfaces() {
		return  Block.generateSurfaces(disabled);
	}
	
	@Override
	public boolean onMetadataUpdate(String key, String value) {
		if(key.equals("active")) {
			for(Object3D obj : Objects) {
				if(obj instanceof Polygon3D poly) {
					if(value.equals("1")) {
						poly.s.c.set(enabled);
					}else {
						poly.s.c.set(disabled);
					}
					poly.recalcLightedColor();
				}
			}
		}
		return false;
	}

}
