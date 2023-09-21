package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public abstract class SignalConsumerComponent extends SignalComponent {

	public SignalConsumerComponent(Block block) {
		super(block);
	}
	
	public void addSignal(int intensity, BlockFace source) {
		int previousSignal = getCharge();
		
		signals.put(source, intensity);
		
		if(previousSignal > 0 && getCharge()==0) {
			turnOff();
		}else if(previousSignal == 0 && getCharge() == 0) {
			turnOn();
		}
	}
	
	public void turnOn() {
		
	}
	
	public void turnOff() {
		
	}
	

}
