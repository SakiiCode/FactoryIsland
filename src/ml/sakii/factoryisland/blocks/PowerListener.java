package ml.sakii.factoryisland.blocks;

public interface PowerListener {
	public void addPower(int power, BlockFace relativeFrom);
	public void removePower(BlockFace relativeFrom);
	
	// IRÁNYELV: körbeellenőrzés sosem, de amilyen hamar átnyomni az energiát
	
}
