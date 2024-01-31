package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.PowerConsumerComponent;

public class TestPowerConsumerBlock extends SimpleMachine{

	PowerConsumerComponent pcc;
	
	private static Color4 disabled = new Color4(50,50,100);
	
	public TestPowerConsumerBlock(int x, int y, int z, GameEngine engine) {
		super("TestPowerConsumer", x, y, z, new Surface(disabled), new Surface(new Color4(0,0,0,255)), AssetLibrary.fire.c, new Color4(Color.GRAY), engine);
		pcc = new PowerConsumerComponent(this);
		addComponent(pcc);
	}

	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfacesCopy(new Surface(disabled,Color.black));
	}
	
	@Override
	public boolean onMetadataUpdate(String key, String value) {
		if(key.equals("power")) {
			storeMetadata(key, value);
			float power = Float.parseFloat(value);
			Main.log("onPowerChange("+power+")");
			for(Object3D obj : Objects){
				if(obj instanceof Polygon3D p) {
					if(power == 0){
						setMetadata("active", "0", true);
					}else{
						setMetadata("active", "1", true);
						
					}
				}
			}
			return true;
		}
		return super.onMetadataUpdate(key, value);
	}


}
