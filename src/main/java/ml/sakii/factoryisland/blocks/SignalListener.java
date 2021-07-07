package ml.sakii.factoryisland.blocks;

public interface SignalListener {
	public void addSignal(int power, BlockFace relativeFrom); 
	public void removeSignal(BlockFace relativeFrom);
	
	
}
