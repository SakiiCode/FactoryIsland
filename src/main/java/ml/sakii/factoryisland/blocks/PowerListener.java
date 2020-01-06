package ml.sakii.factoryisland.blocks;

public interface PowerListener {
	public void addPower(int power, BlockFace relativeFrom); 
	public void removePower(BlockFace relativeFrom);
	
	
}
