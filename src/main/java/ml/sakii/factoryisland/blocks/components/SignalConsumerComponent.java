package ml.sakii.factoryisland.blocks.components;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;

public abstract class SignalConsumerComponent extends SignalComponent {

	public SignalConsumerComponent(Block block) {
		super(block);
	}

	@Override
	public void addSignal(int signal, BlockFace relativeFrom) 
	{
		if(getSignals().get(relativeFrom) == null  || getSignals().get(relativeFrom) < signal){
			if(getCharge()==0) {
				turnOn();
			}
			getSignals().put(relativeFrom, signal);
			//setBlockMeta("signalLevel", getCharge() + "", true);
		}
		

	}

	@Override
	public void removeSignal(BlockFace relativeFrom)
	{
		if(getSignals().containsKey(relativeFrom)){
			if(getCharge()>0) {
				turnOff();
			}
			getSignals().remove(relativeFrom);
			//setBlockMeta("signalLevel", getCharge() + "", true);
		}

	}

	public abstract void turnOn();
	
	public abstract void turnOff();
	
	@Override
	public String toString() {
		return "SignalConsumer(" + getCharge() + ")";
	}

}
